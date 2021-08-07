package com.talla.santhamarket.models;

import java.util.List;
import java.util.Map;

public class DashBoardModel
{
    private List<BannerModel> bannerModelList;
    Map<String, Object> categoriesList;

    public List<BannerModel> getBannerModelList() {
        return bannerModelList;
    }

    public void setBannerModelList(List<BannerModel> bannerModelList) {
        this.bannerModelList = bannerModelList;
    }

    public Map<String, Object> getCategoriesList() {
        return categoriesList;
    }

    public void setCategoriesList(Map<String, Object> categoriesList) {
        this.categoriesList = categoriesList;
    }

    @Override
    public String toString() {
        return "DashBoardModel{" +
                "bannerModelList=" + bannerModelList +
                ", categoriesList=" + categoriesList +
                '}';
    }
}
