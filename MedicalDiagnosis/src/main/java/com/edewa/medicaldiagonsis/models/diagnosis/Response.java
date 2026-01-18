package com.edewa.medicaldiagonsis.models.diagnosis;

import java.util.ArrayList;

public record Response(Issue issue, ArrayList<Specialisation> specialisation) {
}
