package com.academic.service;

import com.academic.model.Mark;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AIService {

    /**
     * Generates a personalised academic advisory report locally —
     * no internet connection or API key required.
     */
    public static CompletableFuture<String> getGuidance(String studentName, List<Mark> marks) {
        return CompletableFuture.supplyAsync(() -> buildReport(studentName, marks));
    }

    private static String buildReport(String studentName, List<Mark> marks) {
        if (marks == null || marks.isEmpty()) {
            return "No results found. Please submit your marks first.";
        }

        // ── Compute summary stats ──────────────────────────────────────
        int    totalCredits = 0;
        double weightedSum  = 0;
        double totalScore   = 0;
        int    passed       = 0;

        List<Mark> excellent = new ArrayList<>();  // A
        List<Mark> good      = new ArrayList<>();  // B
        List<Mark> average   = new ArrayList<>();  // C
        List<Mark> weak      = new ArrayList<>();  // D
        List<Mark> failed    = new ArrayList<>();  // F

        for (Mark m : marks) {
            totalCredits += m.getCreditHours();
            weightedSum  += m.getScore() * m.getCreditHours();
            totalScore   += m.getScore();
            if (m.getScore() >= 50) passed++;

            switch (m.getGrade()) {
                case "A" -> excellent.add(m);
                case "B" -> good.add(m);
                case "C" -> average.add(m);
                case "D" -> weak.add(m);
                default  -> failed.add(m);
            }
        }

        double cwa      = totalCredits > 0 ? weightedSum / totalCredits : 0;
        double avgScore = totalScore / marks.size();
        double passRate = (passed * 100.0) / marks.size();
        String classification = getClassification(cwa);

        StringBuilder sb = new StringBuilder();
        String line = "─".repeat(55);

        // ── Greeting ──────────────────────────────────────────────────
        sb.append("ACADEMIC ADVISORY REPORT\n");
        sb.append(line).append("\n\n");
        sb.append("Dear ").append(studentName).append(",\n\n");
        sb.append(openingStatement(cwa));
        sb.append("\n\n");

        // ── Performance summary ───────────────────────────────────────
        sb.append(line).append("\n");
        sb.append("SEMESTER PERFORMANCE SUMMARY\n");
        sb.append(line).append("\n\n");
        sb.append(String.format("  %-28s %s\n",  "Courses Registered:",   marks.size()));
        sb.append(String.format("  %-28s %d\n",  "Total Credit Hours:",   totalCredits));
        sb.append(String.format("  %-28s %.2f\n","Cumulative Weighted Average (CWA):", cwa));
        sb.append(String.format("  %-28s %.1f\n","Average Score:",        avgScore));
        sb.append(String.format("  %-28s %.0f%%\n","Pass Rate:",          passRate));
        sb.append(String.format("  %-28s %s\n",  "Classification:",       classification));
        sb.append("\n");

        // ── Commendations ─────────────────────────────────────────────
        if (!excellent.isEmpty() || !good.isEmpty()) {
            sb.append(line).append("\n");
            sb.append("COMMENDATIONS\n");
            sb.append(line).append("\n\n");

            for (Mark m : excellent) {
                sb.append("  ✦ ").append(m.getCourseName())
                  .append(" (").append(m.getCourseCode()).append(")")
                  .append(" — Score: ").append(m.getScore()).append(" | Grade: A\n");
                sb.append("    Outstanding performance! You have demonstrated excellent\n");
                sb.append("    mastery of this subject. Keep up this great standard.\n\n");
            }
            for (Mark m : good) {
                sb.append("  ✦ ").append(m.getCourseName())
                  .append(" (").append(m.getCourseCode()).append(")")
                  .append(" — Score: ").append(m.getScore()).append(" | Grade: B\n");
                sb.append("    Very good result! You have a solid understanding of this\n");
                sb.append("    course. A little more effort can push this to an A.\n\n");
            }
        }

        // ── Areas for improvement ─────────────────────────────────────
        if (!average.isEmpty() || !weak.isEmpty() || !failed.isEmpty()) {
            sb.append(line).append("\n");
            sb.append("AREAS FOR IMPROVEMENT\n");
            sb.append(line).append("\n\n");

            for (Mark m : average) {
                sb.append("  ◆ ").append(m.getCourseName())
                  .append(" (").append(m.getCourseCode()).append(")")
                  .append(" — Score: ").append(m.getScore()).append(" | Grade: C\n");
                sb.append("    This is a satisfactory result, but there is clear room to\n");
                sb.append("    improve. Review your lecture notes regularly, attempt past\n");
                sb.append("    questions, and seek help from your lecturer during office hours.\n\n");
            }
            for (Mark m : weak) {
                sb.append("  ◆ ").append(m.getCourseName())
                  .append(" (").append(m.getCourseCode()).append(")")
                  .append(" — Score: ").append(m.getScore()).append(" | Grade: D\n");
                sb.append("    This course needs immediate attention. Form a study group,\n");
                sb.append("    revisit foundational topics, and dedicate extra study hours\n");
                sb.append("    to this subject next semester.\n\n");
            }
            for (Mark m : failed) {
                sb.append("  ✗ ").append(m.getCourseName())
                  .append(" (").append(m.getCourseCode()).append(")")
                  .append(" — Score: ").append(m.getScore()).append(" | Grade: F\n");
                sb.append("    Unfortunately you did not pass this course. Please speak to\n");
                sb.append("    your academic advisor about resit options. Do not be\n");
                sb.append("    discouraged — identify the gaps, seek support, and come\n");
                sb.append("    back stronger next time.\n\n");
            }
        }

        // ── Closing encouragement ─────────────────────────────────────
        sb.append(line).append("\n");
        sb.append("ADVISOR'S NOTE\n");
        sb.append(line).append("\n\n");
        sb.append(closingMessage(cwa, studentName));
        sb.append("\n\n");
        sb.append("  Best wishes,\n");
        sb.append("  IS Academic Advisory Office\n");
        sb.append(line).append("\n");

        return sb.toString();
    }

    // ── Helper text generators ────────────────────────────────────────

    private static String openingStatement(double cwa) {
        if (cwa >= 80)
            return "Congratulations on an outstanding semester! Your results reflect\n" +
                   "exceptional dedication, hard work, and academic excellence. You\n" +
                   "should be very proud of what you have achieved.";
        if (cwa >= 70)
            return "Well done on a very good semester! Your results show a strong\n" +
                   "commitment to your studies. With continued effort, even greater\n" +
                   "achievements are within your reach.";
        if (cwa >= 60)
            return "You have completed this semester with a satisfactory performance.\n" +
                   "While there are areas of strength to celebrate, this report also\n" +
                   "highlights opportunities to improve and push higher next semester.";
        if (cwa >= 50)
            return "You have passed this semester, and that is something to be\n" +
                   "acknowledged. However, your results indicate that with greater\n" +
                   "consistency and effort, you are capable of achieving much more.";
        return "This has been a challenging semester for you. Please do not lose\n" +
               "hope — every student faces difficulties at some point. What matters\n" +
               "now is how you respond and the steps you take to improve.";
    }

    private static String closingMessage(double cwa, String name) {
        if (cwa >= 80)
            return "  " + name + ", your first-class performance this semester is truly\n" +
                   "  commendable. Continue to challenge yourself, maintain your study\n" +
                   "  habits, and you will go far in your academic and professional life.\n" +
                   "  We are proud of you!";
        if (cwa >= 70)
            return "  " + name + ", you have had a great semester. Stay consistent,\n" +
                   "  sharpen your focus on the courses that need attention, and a\n" +
                   "  first-class result is absolutely achievable for you. Keep going!";
        if (cwa >= 60)
            return "  " + name + ", you have the potential to do better and we believe\n" +
                   "  in you. Plan your study schedule, reduce distractions, engage\n" +
                   "  actively in class, and next semester will be significantly stronger.";
        if (cwa >= 50)
            return "  " + name + ", passing is a foundation — now build on it. Speak to\n" +
                   "  your lecturers, attend tutorials, and invest more time in your\n" +
                   "  weakest subjects. You have what it takes to improve significantly.";
        return "  " + name + ", please do not give up. Reach out to your academic\n" +
               "  advisor, explore support resources available to you, and approach\n" +
               "  next semester with a fresh plan and renewed determination.\n" +
               "  Your journey is not over — this is just a challenge to overcome.";
    }

    private static String getClassification(double cwa) {
        if (cwa >= 80) return "First Class";
        if (cwa >= 70) return "Second Class Upper";
        if (cwa >= 60) return "Second Class Lower";
        if (cwa >= 50) return "Pass";
        return "Below Minimum Standard";
    }
}
