package tn.esprit.tn.medicare_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.tn.medicare_ai.dto.PhysicianRecommendationDto;
import tn.esprit.tn.medicare_ai.entity.Role;
import tn.esprit.tn.medicare_ai.entity.User;
import tn.esprit.tn.medicare_ai.entity.VisitNote;
import tn.esprit.tn.medicare_ai.repository.UserRepository;
import tn.esprit.tn.medicare_ai.repository.VisitNoteRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhysicianRecommendationService {

    private final UserRepository userRepository;
    private final VisitNoteRepository visitNoteRepository;

    public List<PhysicianRecommendationDto> recommend(String keywords, int limit) {
        if (keywords == null || keywords.isBlank()) {
            return List.of();
        }
        List<String> tokens = Arrays.stream(keywords.toLowerCase(Locale.ROOT).split("\\s+"))
                .filter(t -> t.length() > 1)
                .distinct()
                .toList();
        if (tokens.isEmpty()) {
            return List.of();
        }

        List<User> doctors = userRepository.findByRole(Role.DOCTOR);
        List<ScoreRow> scored = new ArrayList<>();

        for (User d : doctors) {
            String blob = profileBlob(d);
            for (VisitNote vn : visitNoteRepository.findByDoctor_Id(d.getId())) {
                blob += " " + noteBlob(vn);
            }
            blob = blob.toLowerCase(Locale.ROOT);

            int hits = 0;
            List<String> signals = new ArrayList<>();
            for (String t : tokens) {
                if (blob.contains(t)) {
                    hits++;
                    signals.add("matched '" + t + "'");
                }
            }
            int deptBoost = d.getClinicalDepartment() != null && containsAnyToken(d.getClinicalDepartment().toLowerCase(Locale.ROOT), tokens) ? 18 : 0;
            int kwBoost = d.getClinicalKeywords() != null && containsAnyToken(d.getClinicalKeywords().toLowerCase(Locale.ROOT), tokens) ? 22 : 0;

            int score = Math.min(100, 38 + hits * 14 + deptBoost + kwBoost);
            if (hits > 0 || deptBoost > 0 || kwBoost > 0) {
                String sig = String.join(", ", signals);
                if (deptBoost > 0) {
                    sig += (sig.isEmpty() ? "" : "; ") + "department overlap";
                }
                if (kwBoost > 0) {
                    sig += (sig.isEmpty() ? "" : "; ") + "clinical keywords";
                }
                scored.add(new ScoreRow(d, score, sig.isBlank() ? "profile/clinical match" : sig));
            }
        }

        scored.sort(Comparator.comparingInt((ScoreRow r) -> r.score).reversed());
        return scored.stream().limit(Math.max(1, limit)).map(r -> new PhysicianRecommendationDto(
                r.user.getId(),
                r.user.getFullName(),
                r.user.getEmail(),
                r.user.getClinicalDepartment(),
                r.user.getClinicalKeywords(),
                r.score,
                r.signals
        )).collect(Collectors.toList());
    }

    private boolean containsAnyToken(String text, List<String> tokens) {
        for (String t : tokens) {
            if (text.contains(t)) {
                return true;
            }
        }
        return false;
    }

    private String profileBlob(User d) {
        StringBuilder sb = new StringBuilder();
        sb.append(d.getFullName()).append(' ');
        sb.append(d.getEmail()).append(' ');
        if (d.getClinicalDepartment() != null) {
            sb.append(d.getClinicalDepartment()).append(' ');
        }
        if (d.getClinicalKeywords() != null) {
            sb.append(d.getClinicalKeywords());
        }
        return sb.toString();
    }

    private String noteBlob(VisitNote vn) {
        StringBuilder sb = new StringBuilder();
        if (vn.getSubjective() != null) {
            sb.append(vn.getSubjective()).append(' ');
        }
        if (vn.getObjective() != null) {
            sb.append(vn.getObjective()).append(' ');
        }
        if (vn.getAssessment() != null) {
            sb.append(vn.getAssessment()).append(' ');
        }
        if (vn.getPlan() != null) {
            sb.append(vn.getPlan());
        }
        return sb.toString();
    }

    private record ScoreRow(User user, int score, String signals) {
    }
}
