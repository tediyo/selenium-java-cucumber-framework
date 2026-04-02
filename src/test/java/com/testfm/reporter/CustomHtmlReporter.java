package com.testfm.reporter;

import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.*;

import java.io.IOException;
import java.nio.file.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

/**
 * Custom Cucumber HTML Reporter.
 * Generates a beautiful, self-contained interactive HTML report at the given path.
 *
 * Registered in CucumberRunnerTest as:
 *   com.testfm.reporter.CustomHtmlReporter:target/custom-reports
 *
 * Output: target/custom-reports/{FeatureName}_Report.html
 */
public class CustomHtmlReporter implements EventListener {

    private final String outputPath;
    private final List<FeatureData> features   = new ArrayList<>();
    private final Map<String, FeatureData> map = new LinkedHashMap<>();
    private Instant runStart;
    private Instant runEnd;

    // ── DTOs ───────────────────────────────────────────────────────────────────

    static class StepData {
        String keyword, text, status, error;
        long durationNanos;
    }

    static class ScenarioData {
        String name, status;
        List<String> tags  = new ArrayList<>();
        List<StepData> steps = new ArrayList<>();
        long durationNanos;
    }

    static class FeatureData {
        String name, uri;
        List<ScenarioData> scenarios = new ArrayList<>();
        ScenarioData current;
    }

    // ── Constructor (Cucumber passes the output path here) ─────────────────────

    public CustomHtmlReporter(String outputPath) {
        this.outputPath = outputPath;
    }

    // ── Event wiring ────────────────────────────────────────────────────────────

    @Override
    public void setEventPublisher(EventPublisher pub) {
        pub.registerHandlerFor(TestRunStarted.class,   e -> runStart = e.getInstant());
        pub.registerHandlerFor(TestSourceRead.class,   this::onSource);
        pub.registerHandlerFor(TestCaseStarted.class,  this::onCaseStart);
        pub.registerHandlerFor(TestStepFinished.class, this::onStep);
        pub.registerHandlerFor(TestCaseFinished.class, this::onCaseEnd);
        pub.registerHandlerFor(TestRunFinished.class,  e -> { runEnd = e.getInstant(); generate(); });
    }

    private void onSource(TestSourceRead e) {
        map.computeIfAbsent(e.getUri().toString(), uri -> {
            FeatureData f = new FeatureData();
            f.uri  = uri;
            f.name = baseName(uri);
            features.add(f);
            return f;
        });
    }

    private void onCaseStart(TestCaseStarted e) {
        String uri = e.getTestCase().getUri().toString();
        FeatureData f = map.computeIfAbsent(uri, k -> {
            FeatureData fd = new FeatureData(); fd.uri = k; fd.name = baseName(k);
            features.add(fd); return fd;
        });
        ScenarioData s = new ScenarioData();
        s.name = e.getTestCase().getName();
        s.tags = new ArrayList<>(e.getTestCase().getTags());
        f.current = s;
        f.scenarios.add(s);
    }

    private void onStep(TestStepFinished e) {
        if (!(e.getTestStep() instanceof PickleStepTestStep)) return;
        PickleStepTestStep ps = (PickleStepTestStep) e.getTestStep();
        FeatureData f = map.get(e.getTestCase().getUri().toString());
        if (f == null || f.current == null) return;
        StepData sd = new StepData();
        sd.keyword       = ps.getStep().getKeyword().trim();
        sd.text          = ps.getStep().getText();
        sd.status        = e.getResult().getStatus().name();
        sd.durationNanos = e.getResult().getDuration().toNanos();
        if (e.getResult().getError() != null)
            sd.error = e.getResult().getError().getMessage();
        f.current.steps.add(sd);
    }

    private void onCaseEnd(TestCaseFinished e) {
        FeatureData f = map.get(e.getTestCase().getUri().toString());
        if (f != null && f.current != null) {
            f.current.status        = e.getResult().getStatus().name();
            f.current.durationNanos = e.getResult().getDuration().toNanos();
        }
    }

    // ── Report generation ───────────────────────────────────────────────────────

    private void generate() {
        try {
            Path configured = Paths.get(outputPath);
            Path outDir = isHtmlFile(configured) ? configured.getParent() : configured;
            if (outDir == null) outDir = Paths.get("target", "custom-reports");
            Files.createDirectories(outDir);

            long   ms   = runEnd != null && runStart != null
                          ? Duration.between(runStart, runEnd).toMillis() : 0;
            String date = DateTimeFormatter
                          .ofPattern("dd MMM yyyy, HH:mm:ss", Locale.ENGLISH)
                          .format(LocalDateTime.now());

            List<Path> generated = new ArrayList<>();
            for (FeatureData f : features) {
                FeatureData filtered = filterExecutedScenarios(f);
                if (filtered.scenarios.isEmpty()) continue; // don't generate reports for fully-skipped features

                Path outFile = outDir.resolve(fileSafeName(displayName(filtered.name)) + "_Report.html");
                Files.writeString(outFile, buildHtmlForFeature(filtered, ms, date));
                generated.add(outFile);
            }

            String sep = "═".repeat(50);
            System.out.println("\n\033[1;96m╔" + sep + "╗\033[0m");
            System.out.println("\033[1;96m║\033[0m  \uD83D\uDCCA  Custom HTML Report Generated" + " ".repeat(18) + "\033[1;96m║\033[0m");
            System.out.println("\033[1;96m╠" + sep + "╣\033[0m");
            if (generated.isEmpty()) {
                System.out.printf("\033[1;96m║\033[0m  \uD83D\uDCC1  %-46s\033[1;96m║\033[0m%n",
                        outDir.toAbsolutePath());
            } else {
                // Print the first file path in the fancy box
                System.out.printf("\033[1;96m║\033[0m  \uD83D\uDCC1  %-46s\033[1;96m║\033[0m%n",
                        generated.get(0).toAbsolutePath());
            }
            System.out.println("\033[1;96m╚" + sep + "╝\033[0m\n");
            if (generated.size() > 1) {
                for (int i = 1; i < generated.size(); i++) {
                    System.out.println("[CustomHtmlReporter] Also generated: " + generated.get(i).toAbsolutePath());
                }
            }

        } catch (IOException ex) {
            System.err.println("[CustomHtmlReporter] ERROR: " + ex.getMessage());
        }
    }

    // ── HTML builder ─────────────────────────────────────────────────────────────

    private String buildHtmlForFeature(FeatureData feature, long ms, String date) {
        int total = 0, passed = 0, failed = 0, skipped = 0;
        Set<String> allTags = new LinkedHashSet<>();
        for (ScenarioData s : feature.scenarios) {
            total++;
            if ("PASSED".equals(s.status))       passed++;
            else if ("FAILED".equals(s.status))  failed++;
            else                                  skipped++;
            allTags.addAll(s.tags);
        }

        String overall  = failed > 0 ? "FAILED" : "PASSED";
        String passRate = total > 0
                          ? String.format("%.0f", passed * 100.0 / total) : "0";

        String featuresHtml = featureBlock(feature, 0);

        // ── tag filter buttons ─────────────────────────────────────────────────
        StringBuilder tagBtns = new StringBuilder();
        tagBtns.append("<button class=\"tbtn active\" onclick=\"filterTag('all',this)\">All</button>\n");
        for (String t : allTags)
            tagBtns.append("<button class=\"tbtn\" onclick=\"filterTag('")
                   .append(esc(t)).append("',this)\">").append(esc(t)).append("</button>\n");

        return "<!DOCTYPE html>\n<html lang=\"en\" data-theme=\"dark\">\n<head>\n"
            + "<meta charset=\"UTF-8\">\n"
            + "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">\n"
            + "<title>" + esc(displayName(feature.name)) + " Report</title>\n"
            + "<script src=\"https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js\"></script>\n"
            + "<style>\n" + CSS + "\n</style>\n"
            + "</head>\n<body>\n"

            // ── header ────────────────────────────────────────────────────────
            + "<header>\n"
            + "  <div class=\"hi\">\n"
            + "    <div class=\"brand\"><span class=\"bicon\">⬡</span>"
            + "<span class=\"bname\">" + esc(displayName(feature.name)) + "</span><span class=\"bsub\">Report</span></div>\n"
            + "    <div class=\"hmeta\">\n"
            + "      <span class=\"obadge " + overall.toLowerCase() + "\">" + overall + "</span>\n"
            + "      <span class=\"rdate\">🕒 " + date + "</span>\n"
            + "      <button class=\"theme-btn\" id=\"themeToggle\" onclick=\"toggleTheme()\" title=\"Toggle theme\">\n"
            + "        <span class=\"theme-btn-inner\" id=\"themeBtnContent\">\n"
            + ICON_MOON_SVG
            + "          <span class=\"theme-label\">Light</span>\n"
            + "        </span>\n"
            + "      </button>\n"
            + "    </div>\n"
            + "  </div>\n"
            + "</header>\n<main>\n"

            // ── stat cards ────────────────────────────────────────────────────
            + "<section class=\"summary\">\n"
            + statCard("Total",   String.valueOf(total),          "sc-total")
            + statCard("Passed",  String.valueOf(passed),         "sc-pass")
            + statCard("Failed",  String.valueOf(failed),         "sc-fail")
            + statCard("Skipped", String.valueOf(skipped),        "sc-skip")
            + statCard("Duration",fmtMs(ms),                      "sc-dur")
            + "</section>\n"

            // ── donut chart ───────────────────────────────────────────────────
            + "<section class=\"chartrow\">\n"
            + "  <div class=\"card ccard\">\n"
            + "    <div class=\"cwrap\">\n"
            + "      <canvas id=\"donut\" width=\"200\" height=\"200\"></canvas>\n"
            + "      <div class=\"ccenter\"><span class=\"cpct\">" + passRate + "%</span>"
            + "<span class=\"clbl\">Pass Rate</span></div>\n"
            + "    </div>\n"
            + "    <div class=\"cleg\">\n"
            + "      <span class=\"ldot ld-p\"></span><span>Passed (" + passed + ")</span>\n"
            + "      <span class=\"ldot ld-f\"></span><span>Failed (" + failed + ")</span>\n"
            + "      <span class=\"ldot ld-s\"></span><span>Skipped (" + skipped + ")</span>\n"
            + "    </div>\n"
            + "  </div>\n"
            + "</section>\n"

            // ── controls ──────────────────────────────────────────────────────
            + "<section class=\"controls\">\n"
            + "  <div class=\"sbox\"><span class=\"sico\">🔍</span>"
            + "<input id=\"qi\" type=\"text\" placeholder=\"Search scenarios…\" oninput=\"applyFilters()\"></div>\n"
            + "  <div class=\"tfilters\">" + tagBtns + "</div>\n"
            + "</section>\n"

            // ── features ──────────────────────────────────────────────────────
            + "<section class=\"features\">\n" + featuresHtml + "</section>\n"
            + "</main>\n"
            + "<footer><span>Generated by TestFM Custom Reporter&nbsp;•&nbsp;" + date + "</span></footer>\n"
            + "<script>\n" + buildJs(passed, failed, skipped) + "</script>\n"
            + "</body>\n</html>\n";
    }

    private String statCard(String label, String value, String cls) {
        return "<div class=\"card sc " + cls + "\">"
             + "<div class=\"scn\">" + value + "</div>"
             + "<div class=\"scl\">" + label + "</div></div>\n";
    }

    // ── feature / scenario blocks ─────────────────────────────────────────────

    private String featureBlock(FeatureData f, int fi) {
        int fp = 0, ff = 0;
        for (ScenarioData s : f.scenarios) {
            if ("PASSED".equals(s.status)) fp++;
            else if ("FAILED".equals(s.status)) ff++;
        }
        String fst = ff > 0 ? "failed" : "passed";

        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"fb\" data-fi=\"").append(fi).append("\">\n")
          .append("  <div class=\"fh\" onclick=\"toggleF(").append(fi).append(")\">\n")
          .append("    <span class=\"ftog\" id=\"ft").append(fi).append("\">▼</span>\n")
          .append("    <span class=\"ficon\">📋</span>\n")
          .append("    <span class=\"fname\">").append(esc(displayName(f.name))).append("</span>\n")
          .append("    <div class=\"fbadges\">\n")
          .append("      <span class=\"badge bpass\">✔ ").append(fp).append("</span>\n");
        if (ff > 0)
            sb.append("      <span class=\"badge bfail\">✖ ").append(ff).append("</span>\n");
        sb.append("      <span class=\"fst ").append(fst).append("\">").append(fst.toUpperCase()).append("</span>\n")
          .append("    </div>\n")
          .append("  </div>\n")
          .append("  <div class=\"sw\" id=\"fw").append(fi).append("\">\n");

        for (int si = 0; si < f.scenarios.size(); si++)
            sb.append(scenarioBlock(f.scenarios.get(si), fi, si));

        sb.append("  </div>\n</div>\n");
        return sb.toString();
    }

    private String scenarioBlock(ScenarioData s, int fi, int si) {
        String sid  = "s" + fi + "_" + si;
        String stc  = s.status == null ? "skipped" : s.status.toLowerCase();
        String sico = "PASSED".equals(s.status) ? "✔" : "FAILED".equals(s.status) ? "✖" : "○";

        StringBuilder tags = new StringBuilder();
        for (String t : s.tags)
            tags.append("<span class=\"stag\">").append(esc(t)).append("</span>");

        StringBuilder steps = new StringBuilder();
        long maxNanos = s.steps.stream().mapToLong(st -> st.durationNanos).max().orElse(1);
        for (StepData step : s.steps) {
            String sc  = step.status == null ? "skipped" : step.status.toLowerCase();
            String sic = "PASSED".equals(step.status) ? "✔" : "FAILED".equals(step.status) ? "✖" : "○";
            String pc  = perfClass(step.durationNanos);
            long   pct = maxNanos > 0 ? Math.min(100L, step.durationNanos * 100L / maxNanos) : 4L;
            steps.append("<div class=\"step ").append(sc).append("\">\n")
                 .append("  <div class=\"step-row\">\n")
                 .append("    <span class=\"stico ").append(sc).append("\">").append(sic).append("</span>\n")
                 .append("    <span class=\"stkw\">").append(esc(step.keyword)).append("</span>\n")
                 .append("    <span class=\"sttxt\">").append(esc(step.text)).append("</span>\n")
                 .append("    <span class=\"stdr ").append(pc).append("\">⏱ ").append(fmtNano(step.durationNanos)).append("</span>\n")
                 .append("  </div>\n")
                 .append("  <div class=\"stbar-wrap\"><div class=\"stbar ").append(pc)
                 .append("\" style=\"width:").append(pct).append("%\"></div></div>\n");
            if (step.error != null)
                steps.append("  <div class=\"sterr\"><pre>").append(esc(trunc(step.error, 400))).append("</pre></div>\n");
            steps.append("</div>\n");
        }

        String tagData = String.join(",", s.tags);
        return "<div class=\"scenario\" id=\"" + sid + "\" data-tags=\"" + esc(tagData)
             + "\" data-name=\"" + esc(s.name.toLowerCase()) + "\">\n"
             + "  <div class=\"sh\" onclick=\"toggleS('" + sid + "')\">\n"
             + "    <span class=\"stog\" id=\"st" + sid + "\">▶</span>\n"
             + "    <span class=\"sico " + stc + "\">" + sico + "</span>\n"
             + "    <span class=\"sname\">" + esc(s.name) + "</span>\n"
             + "    <div class=\"smeta\">" + tags
             + "      <span class=\"sdur\">⏱ " + fmtNano(s.durationNanos) + "</span>\n"
             + "      <span class=\"sst " + stc + "\">" + (s.status != null ? s.status : "SKIPPED") + "</span>\n"
             + "    </div>\n"
             + "  </div>\n"
             + "  <div class=\"stepsw\" id=\"sw" + sid + "\" style=\"display:none\">\n"
             + steps
             + "  </div>\n"
             + "</div>\n";
    }

    // ── JavaScript ────────────────────────────────────────────────────────────

    private String buildJs(int passed, int failed, int skipped) {
        return "const ctx=document.getElementById('donut').getContext('2d');\n"
             + "new Chart(ctx,{type:'doughnut',data:{datasets:[{data:["
             + passed + "," + failed + "," + skipped + "],"
             + "backgroundColor:['#22c55e','#ef4444','#f59e0b'],"
             + "borderColor:['#16a34a','#dc2626','#d97706'],"
             + "borderWidth:2,hoverOffset:10}]},"
             + "options:{cutout:'74%',plugins:{legend:{display:false}},"
             + "animation:{animateScale:true,duration:900}}});\n\n"

             // ── theme toggle ─────────────────────────────────────────────────
             + "var MOON_SVG=" + ICON_MOON_JS + ";\n"
             + "var SUN_SVG=" + ICON_SUN_JS + ";\n"
             + "(function(){\n"
             + "  var saved=localStorage.getItem('tfm-theme');\n"
             + "  var theme=saved||(window.matchMedia('(prefers-color-scheme:light)').matches?'light':'dark');\n"
             + "  applyTheme(theme,true);\n"
             + "})();\n"
             + "function applyTheme(theme,init){\n"
             + "  document.documentElement.setAttribute('data-theme',theme);\n"
             + "  localStorage.setItem('tfm-theme',theme);\n"
             + "  var c=document.getElementById('themeBtnContent');\n"
             + "  if(!c)return;\n"
             + "  if(theme==='dark'){\n"
             + "    c.innerHTML=MOON_SVG+'<span class=\"theme-label\">Light</span>';\n"
             + "  } else {\n"
             + "    c.innerHTML=SUN_SVG+'<span class=\"theme-label\">Dark</span>';\n"
             + "  }\n"
             + "}\n"
             + "function toggleTheme(){\n"
             + "  var cur=document.documentElement.getAttribute('data-theme');\n"
             + "  applyTheme(cur==='dark'?'light':'dark',false);\n"
             + "}\n\n"

             // ── toggle feature ────────────────────────────────────────────────
             + "function toggleF(i){\n"
             + "  const w=document.getElementById('fw'+i),t=document.getElementById('ft'+i);\n"
             + "  const o=w.style.display!=='none';w.style.display=o?'none':'block';\n"
             + "  t.textContent=o?'▶':'▼';\n"
             + "}\n"
             + "function toggleS(id){\n"
             + "  const w=document.getElementById('sw'+id),t=document.getElementById('st'+id);\n"
             + "  const o=w.style.display!=='none';w.style.display=o?'none':'block';\n"
             + "  t.textContent=o?'▶':'▼';\n"
             + "}\n"
             + "let activeTag='all';\n"
             + "function filterTag(tag,btn){\n"
             + "  activeTag=tag;\n"
             + "  document.querySelectorAll('.tbtn').forEach(b=>b.classList.remove('active'));\n"
             + "  btn.classList.add('active');\n"
             + "  applyFilters();\n"
             + "}\n"
             + "function applyFilters(){\n"
             + "  const q=document.getElementById('qi').value.toLowerCase();\n"
             + "  document.querySelectorAll('.scenario').forEach(el=>{\n"
             + "    const n=el.dataset.name||'',tags=el.dataset.tags||'';\n"
             + "    const mt=activeTag==='all'||tags.includes(activeTag);\n"
             + "    const mq=!q||n.includes(q);\n"
             + "    el.style.display=(mt&&mq)?'':'none';\n"
             + "  });\n"
             + "  document.querySelectorAll('.fb').forEach(fb=>{\n"
             + "    const vis=[...fb.querySelectorAll('.scenario')].some(s=>s.style.display!=='none');\n"
             + "    fb.style.display=vis?'':'none';\n"
             + "  });\n"
             + "}\n";
    }

    // ── SVG Icons ────────────────────────────────────────────────────────────

    /** Moon icon – shown when in dark mode (click → switch to light) */
    private static final String ICON_MOON_SVG =
        "          <svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" "
      + "stroke-linecap=\"round\" stroke-linejoin=\"round\">"
      + "<path d=\"M21 12.79A9 9 0 1 1 11.21 3a7 7 0 0 0 9.79 9.79z\"/></svg>\n";

    /** Sun icon – shown when in light mode (click → switch to dark) */
    // (Not used directly; JS-safe version below is used for the toggle button)

    /** JS-safe versions (single-quoted strings for innerHTML injection) */
    private static final String ICON_MOON_JS =
        "'<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" "
      + "stroke-linecap=\"round\" stroke-linejoin=\"round\">"
      + "<path d=\"M21 12.79A9 9 0 1 1 11.21 3a7 7 0 0 0 9.79 9.79z\"/></svg>'";

    private static final String ICON_SUN_JS =
        "'<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" "
      + "stroke-linecap=\"round\" stroke-linejoin=\"round\">"
      + "<circle cx=\"12\" cy=\"12\" r=\"5\"/>"
      + "<line x1=\"12\" y1=\"1\" x2=\"12\" y2=\"3\"/>"
      + "<line x1=\"12\" y1=\"21\" x2=\"12\" y2=\"23\"/>"
      + "<line x1=\"4.22\" y1=\"4.22\" x2=\"5.64\" y2=\"5.64\"/>"
      + "<line x1=\"18.36\" y1=\"18.36\" x2=\"19.78\" y2=\"19.78\"/>"
      + "<line x1=\"1\" y1=\"12\" x2=\"3\" y2=\"12\"/>"
      + "<line x1=\"21\" y1=\"12\" x2=\"23\" y2=\"12\"/>"
      + "<line x1=\"4.22\" y1=\"19.78\" x2=\"5.64\" y2=\"18.36\"/>"
      + "<line x1=\"18.36\" y1=\"5.64\" x2=\"19.78\" y2=\"4.22\"/>"
      + "</svg>'";

    // ── CSS ──────────────────────────────────────────────────────────────────

    private static final String CSS =
        "@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');\n"

      // ── CSS custom properties (design tokens) ────────────────────────────────
      + ":root{\n"
      + "  --bg:#0d1117;--surface:rgba(22,27,34,.8);--surface-alt:rgba(13,17,23,.5);\n"
      + "  --border:rgba(255,255,255,.07);--border-hover:rgba(99,102,241,.4);\n"
      + "  --txt:#e6edf3;--txt-muted:#8b949e;--txt-body:#c9d1d9;\n"
      + "  --header-bg:linear-gradient(135deg,#161b22 0%,#1a2030 100%);\n"
      + "  --header-border:rgba(255,255,255,.08);\n"
      + "  --input-bg:rgba(22,27,34,.9);--input-border:rgba(255,255,255,.1);\n"
      + "  --ftog-bg:rgba(255,255,255,.02);--ftog-hover:rgba(255,255,255,.04);\n"
      + "  --sc-bg:rgba(13,17,23,.5);--sc-border:rgba(255,255,255,.05);\n"
      + "  --sh-hover:rgba(255,255,255,.03);\n"
      + "  --step-hover:rgba(255,255,255,.02);\n"
      + "  --stbar-wrap:rgba(255,255,255,.07);\n"
      + "  --footer-border:rgba(255,255,255,.06);--footer-txt:#6e7681;\n"
      + "}\n"

      // light theme overrides
      + "[data-theme=light]{\n"
      + "  --bg:#f0f4f8;--surface:rgba(255,255,255,.95);--surface-alt:rgba(241,245,249,.9);\n"
      + "  --border:rgba(0,0,0,.08);--border-hover:rgba(99,102,241,.5);\n"
      + "  --txt:#111827;--txt-muted:#6b7280;--txt-body:#374151;\n"
      + "  --header-bg:linear-gradient(135deg,#4f46e5 0%,#7c3aed 100%);\n"
      + "  --header-border:rgba(0,0,0,.1);\n"
      + "  --input-bg:rgba(255,255,255,.9);--input-border:rgba(0,0,0,.12);\n"
      + "  --ftog-bg:rgba(0,0,0,.02);--ftog-hover:rgba(0,0,0,.04);\n"
      + "  --sc-bg:rgba(248,250,252,.9);--sc-border:rgba(0,0,0,.06);\n"
      + "  --sh-hover:rgba(0,0,0,.03);\n"
      + "  --step-hover:rgba(0,0,0,.02);\n"
      + "  --stbar-wrap:rgba(0,0,0,.08);\n"
      + "  --footer-border:rgba(0,0,0,.08);--footer-txt:#9ca3af;\n"
      + "}\n"

      + "*{margin:0;padding:0;box-sizing:border-box}\n"
      + "body{font-family:'Inter',sans-serif;background:var(--bg);color:var(--txt);min-height:100vh;"
      +   "display:flex;flex-direction:column;transition:background .25s,color .25s}\n"
      + "a{color:#6366f1}\n"

      // header
      + "header{background:var(--header-bg);"
      +   "border-bottom:1px solid var(--header-border);padding:0 32px;position:sticky;top:0;z-index:100;"
      +   "backdrop-filter:blur(12px);transition:background .25s}\n"
      + ".hi{max-width:1200px;margin:0 auto;height:64px;display:flex;align-items:center;justify-content:space-between}\n"
      + ".brand{display:flex;align-items:center;gap:10px}\n"
      + ".bicon{font-size:28px;background:linear-gradient(135deg,#6366f1,#8b5cf6);-webkit-background-clip:text;"
      +   "-webkit-text-fill-color:transparent;background-clip:text}\n"
      + ".bname{font-size:20px;font-weight:700;letter-spacing:.5px;color:#fff}\n"
      + ".bsub{font-size:13px;color:rgba(255,255,255,.65);margin-left:4px;font-weight:400}\n"
      + ".hmeta{display:flex;align-items:center;gap:12px}\n"
      + ".obadge{padding:4px 14px;border-radius:20px;font-size:12px;font-weight:600;letter-spacing:.8px}\n"
      + ".obadge.passed{background:rgba(34,197,94,.2);color:#22c55e;border:1px solid rgba(34,197,94,.4)}\n"
      + ".obadge.failed{background:rgba(239,68,68,.2);color:#ef4444;border:1px solid rgba(239,68,68,.4)}\n"
      + ".rdate{font-size:13px;color:rgba(255,255,255,.7)}\n"

      // theme toggle button
      + ".theme-btn{background:rgba(255,255,255,.12);border:1px solid rgba(255,255,255,.22);"
      +   "border-radius:24px;padding:6px 14px 6px 10px;cursor:pointer;"
      +   "transition:background .2s,transform .15s,box-shadow .2s;"
      +   "color:#fff;display:inline-flex;align-items:center;gap:0;"
      +   "font-family:inherit;white-space:nowrap}\n"
      + ".theme-btn:hover{background:rgba(255,255,255,.22);transform:scale(1.04);"
      +   "box-shadow:0 2px 12px rgba(0,0,0,.25)}\n"
      + ".theme-btn:active{transform:scale(.97)}\n"
      + ".theme-btn-inner{display:inline-flex;align-items:center;gap:7px}\n"
      + ".theme-btn-inner svg{width:16px;height:16px;flex-shrink:0}\n"
      + ".theme-label{font-size:12px;font-weight:600;letter-spacing:.5px}\n"
      + "[data-theme=light] .theme-btn{background:rgba(0,0,0,.1);border-color:rgba(0,0,0,.15);color:#1e1b4b}\n"
      + "[data-theme=light] .theme-btn:hover{background:rgba(0,0,0,.15)}\n"

      // main
      + "main{max-width:1200px;margin:0 auto;width:100%;padding:32px 24px;flex:1}\n"

      // summary cards
      + ".summary{display:grid;grid-template-columns:repeat(auto-fit,minmax(160px,1fr));gap:16px;margin-bottom:24px}\n"
      + ".card{background:var(--surface);border:1px solid var(--border);border-radius:12px;"
      +   "padding:20px 24px;backdrop-filter:blur(8px);transition:transform .2s,border-color .2s,background .25s}\n"
      + ".card:hover{transform:translateY(-2px);border-color:var(--border-hover)}\n"
      + ".sc{text-align:center}\n"
      + ".scn{font-size:32px;font-weight:700;line-height:1}\n"
      + ".scl{font-size:12px;color:var(--txt-muted);margin-top:6px;text-transform:uppercase;letter-spacing:.8px}\n"
      + ".sc-total .scn{color:var(--txt)}\n"
      + ".sc-pass  .scn{color:#22c55e}\n"
      + ".sc-fail  .scn{color:#ef4444}\n"
      + ".sc-skip  .scn{color:#f59e0b}\n"
      + ".sc-dur   .scn{color:#6366f1;font-size:24px}\n"

      // chart
      + ".chartrow{margin-bottom:24px}\n"
      + ".ccard{display:flex;align-items:center;gap:40px;padding:28px 32px}\n"
      + ".cwrap{position:relative;width:160px;height:160px;flex-shrink:0}\n"
      + ".cwrap canvas{position:absolute;top:0;left:0}\n"
      + ".ccenter{position:absolute;top:50%;left:50%;transform:translate(-50%,-50%);"
      +   "text-align:center;pointer-events:none}\n"
      + ".cpct{display:block;font-size:28px;font-weight:700;color:var(--txt)}\n"
      + ".clbl{display:block;font-size:11px;color:var(--txt-muted);margin-top:2px;text-transform:uppercase;letter-spacing:.6px}\n"
      + ".cleg{display:flex;flex-direction:column;gap:10px}\n"
      + ".ldot{display:inline-block;width:10px;height:10px;border-radius:50%;margin-right:8px}\n"
      + ".ld-p{background:#22c55e}.ld-f{background:#ef4444}.ld-s{background:#f59e0b}\n"
      + ".cleg span{font-size:13px;color:var(--txt-body);vertical-align:middle}\n"

      // controls
      + ".controls{display:flex;flex-wrap:wrap;gap:14px;align-items:center;margin-bottom:24px}\n"
      + ".sbox{display:flex;align-items:center;gap:8px;background:var(--input-bg);"
      +   "border:1px solid var(--input-border);border-radius:8px;padding:8px 14px;flex:1;min-width:220px;"
      +   "transition:background .25s,border-color .25s}\n"
      + ".sico{font-size:15px;opacity:.6}\n"
      + ".sbox input{background:none;border:none;outline:none;color:var(--txt);font-size:14px;width:100%;"
      +   "font-family:inherit}\n"
      + ".sbox input::placeholder{color:var(--txt-muted)}\n"
      + ".tfilters{display:flex;flex-wrap:wrap;gap:8px}\n"
      + ".tbtn{background:var(--surface);border:1px solid var(--border);border-radius:20px;"
      +   "color:var(--txt-body);font-size:12px;padding:5px 14px;cursor:pointer;"
      +   "transition:all .2s;font-family:inherit}\n"
      + ".tbtn:hover{background:rgba(99,102,241,.15);border-color:rgba(99,102,241,.5);color:#6366f1}\n"
      + ".tbtn.active{background:rgba(99,102,241,.2);border-color:#6366f1;color:#6366f1;font-weight:600}\n"

      // features
      + ".features{display:flex;flex-direction:column;gap:14px}\n"
      + ".fb{border:1px solid var(--border);border-radius:12px;overflow:hidden;"
      +   "background:var(--surface);transition:border-color .2s,background .25s}\n"
      + ".fb:hover{border-color:var(--border-hover)}\n"
      + ".fh{display:flex;align-items:center;gap:10px;padding:16px 20px;cursor:pointer;"
      +   "user-select:none;background:var(--ftog-bg);transition:background .2s}\n"
      + ".fh:hover{background:var(--ftog-hover)}\n"
      + ".ftog{font-size:11px;color:var(--txt-muted);width:14px;flex-shrink:0;transition:transform .2s}\n"
      + ".ficon{font-size:16px}\n"
      + ".fname{font-size:15px;font-weight:600;flex:1;text-transform:capitalize;color:var(--txt)}\n"
      + ".fbadges{display:flex;align-items:center;gap:8px}\n"
      + ".badge{font-size:11px;font-weight:600;padding:3px 10px;border-radius:12px}\n"
      + ".bpass{background:rgba(34,197,94,.12);color:#22c55e;border:1px solid rgba(34,197,94,.25)}\n"
      + ".bfail{background:rgba(239,68,68,.12);color:#ef4444;border:1px solid rgba(239,68,68,.25)}\n"
      + ".fst{font-size:11px;font-weight:700;padding:3px 10px;border-radius:12px;letter-spacing:.6px}\n"
      + ".fst.passed{background:rgba(34,197,94,.1);color:#22c55e}\n"
      + ".fst.failed{background:rgba(239,68,68,.1);color:#ef4444}\n"
      + ".sw{padding:0 12px 12px}\n"

      // scenario
      + ".scenario{border:1px solid var(--sc-border);border-radius:8px;margin-top:8px;"
      +   "overflow:hidden;background:var(--sc-bg);transition:background .25s}\n"
      + ".sh{display:flex;align-items:center;gap:10px;padding:12px 16px;cursor:pointer;"
      +   "user-select:none;transition:background .15s}\n"
      + ".sh:hover{background:var(--sh-hover)}\n"
      + ".stog{font-size:10px;color:var(--txt-muted);width:12px;flex-shrink:0}\n"
      + ".sico{width:20px;height:20px;border-radius:50%;display:inline-flex;align-items:center;"
      +   "justify-content:center;font-size:11px;font-weight:700;flex-shrink:0}\n"
      + ".sico.passed{background:rgba(34,197,94,.2);color:#22c55e}\n"
      + ".sico.failed{background:rgba(239,68,68,.2);color:#ef4444}\n"
      + ".sico.skipped{background:rgba(245,158,11,.2);color:#f59e0b}\n"
      + ".sname{font-size:14px;font-weight:500;flex:1;color:var(--txt)}\n"
      + ".smeta{display:flex;align-items:center;gap:8px;flex-shrink:0}\n"
      + ".stag{font-size:11px;background:rgba(99,102,241,.15);color:#a5b4fc;"
      +   "border:1px solid rgba(99,102,241,.25);border-radius:10px;padding:2px 8px}\n"
      + ".sdur{font-size:12px;color:var(--txt-muted)}\n"
      + ".sst{font-size:11px;font-weight:600;padding:2px 8px;border-radius:10px}\n"
      + ".sst.passed{background:rgba(34,197,94,.1);color:#22c55e}\n"
      + ".sst.failed{background:rgba(239,68,68,.1);color:#ef4444}\n"
      + ".sst.skipped{background:rgba(245,158,11,.1);color:#f59e0b}\n"

      // steps
      + ".stepsw{padding:0 16px 14px;border-top:1px solid var(--border);margin-top:0}\n"
      + ".step{display:flex;flex-direction:column;padding:7px 10px;border-radius:6px;margin-top:4px;"
      +   "font-size:13px;transition:background .15s}\n"
      + ".step:hover{background:var(--step-hover)}\n"
      + ".step.passed{border-left:3px solid rgba(34,197,94,.5)}\n"
      + ".step.failed{border-left:3px solid rgba(239,68,68,.5);background:rgba(239,68,68,.04)}\n"
      + ".step.skipped{border-left:3px solid rgba(245,158,11,.4)}\n"
      + ".step-row{display:flex;align-items:center;gap:8px;width:100%}\n"
      + ".stico{font-size:11px;font-weight:700;width:14px;flex-shrink:0}\n"
      + ".stico.passed{color:#22c55e}.stico.failed{color:#ef4444}.stico.skipped{color:#f59e0b}\n"
      + ".stkw{font-weight:600;color:#6366f1;min-width:40px;flex-shrink:0}\n"
      + ".sttxt{flex:1;color:var(--txt-body)}\n"
      + ".stdr{font-size:11px;font-weight:600;padding:2px 9px;border-radius:10px;flex-shrink:0;white-space:nowrap}\n"
      + ".stdr.perf-fast{background:rgba(34,197,94,.12);color:#4ade80;border:1px solid rgba(34,197,94,.25)}\n"
      + ".stdr.perf-med{background:rgba(245,158,11,.12);color:#fbbf24;border:1px solid rgba(245,158,11,.25)}\n"
      + ".stdr.perf-slow{background:rgba(239,68,68,.12);color:#f87171;border:1px solid rgba(239,68,68,.25)}\n"
      + ".stbar-wrap{height:3px;background:var(--stbar-wrap);border-radius:2px;margin:5px 0 2px 22px}\n"
      + ".stbar{height:3px;border-radius:2px;min-width:4px;transition:width .7s ease}\n"
      + ".stbar.perf-fast{background:linear-gradient(90deg,#16a34a,#4ade80)}\n"
      + ".stbar.perf-med{background:linear-gradient(90deg,#d97706,#fbbf24)}\n"
      + ".stbar.perf-slow{background:linear-gradient(90deg,#dc2626,#f87171)}\n"
      + ".sterr{margin-top:6px;width:100%}\n"
      + ".sterr pre{background:rgba(239,68,68,.08);border:1px solid rgba(239,68,68,.2);"
      +   "border-radius:6px;padding:10px 12px;font-size:11px;color:#fca5a5;"
      +   "overflow-x:auto;white-space:pre-wrap;word-break:break-word;line-height:1.5}\n"

      // footer
      + "footer{text-align:center;padding:20px;font-size:12px;color:var(--footer-txt);"
      +   "border-top:1px solid var(--footer-border);margin-top:auto;transition:color .25s}\n"
      ;

    // ── Utilities ─────────────────────────────────────────────────────────────

    private boolean isHtmlFile(Path p) {
        if (p == null) return false;
        String s = p.toString().toLowerCase(Locale.ROOT);
        return s.endsWith(".html") || s.endsWith(".htm");
    }

    private String fileSafeName(String s) {
        if (s == null) return "Report";
        // Windows reserved chars: \ / : * ? " < > |
        String cleaned = s.replaceAll("[\\\\/:*?\"<>|]+", "_").trim();
        if (cleaned.isBlank()) return "Report";
        return cleaned;
    }

    private String baseName(String uri) {
        String[] p = uri.split("[/\\\\]");
        return p[p.length - 1].replace(".feature", "");
    }

    private FeatureData filterExecutedScenarios(FeatureData f) {
        FeatureData out = new FeatureData();
        out.name = f.name;
        out.uri = f.uri;
        for (ScenarioData s : f.scenarios) {
            // When running with tag filters, non-matching scenarios can appear as skipped.
            if (s.status == null) continue;
            if ("SKIPPED".equalsIgnoreCase(s.status)) continue;
            out.scenarios.add(s);
        }
        return out;
    }

    private String displayName(String n) {
        return n.replace("_", " ").replace("-", " ");
    }

    private String fmtMs(long ms) {
        if (ms < 1000) return ms + "ms";
        long s = ms / 1000;
        return s < 60 ? s + "s" : (s / 60) + "m " + (s % 60) + "s";
    }

    private String fmtNano(long nanos) {
        if (nanos < 1_000_000) return (nanos / 1_000) + "µs";
        long ms = nanos / 1_000_000;
        return ms < 1000 ? ms + "ms" : String.format("%.1fs", ms / 1000.0);
    }

    /**
     * Maps step duration to a CSS perf class:
     *   perf-fast  = green  ( < 500 ms )
     *   perf-med   = amber  ( 500 ms – 2 s )
     *   perf-slow  = red    ( ≥ 2 s )
     */
    private String perfClass(long nanos) {
        long ms = nanos / 1_000_000;
        if (ms < 500)  return "perf-fast";
        if (ms < 2000) return "perf-med";
        return "perf-slow";
    }

    private String trunc(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max) + "…";
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")
                .replace("\"","&quot;").replace("'","&#39;");
    }
}
