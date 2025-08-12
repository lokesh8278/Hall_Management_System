package com.hallbooking.vendor.dto;

import java.util.List;

public class UpdateFeaturesRequest {
    private List<String> features;

    public UpdateFeaturesRequest() { }

    public UpdateFeaturesRequest(List<String> features) {
        this.features = features;
    }

    public List<String> getFeatures() {
        return features;
    }
    public void setFeatures(List<String> features) {
        this.features = features;
    }
}
