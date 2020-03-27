package com.group4sweng.scranplan.UserInfo;

import java.io.Serializable;

/**
 * Preferences class
 * Used within UserInfo class to save specific preferences user wants to apply to all queries within
 * the application
 */
public class Preferences implements Serializable, FilterType {

    // All preference variables
    private boolean allergy_celery;
    private boolean allergy_crustacean;
    private boolean allergy_eggs;
    private boolean allergy_fish;
    private boolean allergy_gluten;
    private boolean allergy_milk;
    private boolean allergy_mustard;
    private boolean allergy_nuts;
    private boolean allergy_peanuts;
    private boolean allergy_sesame;
    private boolean allergy_shellfish;
    private boolean allergy_soya;
    private boolean allergy_sulphide;
    private boolean diabetic;
    private boolean halal;
    private boolean high_protein;
    private boolean kosher;
    private boolean lactose_free;
    private boolean lactovegetarian;
    private boolean low_carb;
    private boolean low_sodium;
    private boolean no_alcohol;
    private boolean no_pork;
    private boolean ovovegetarian;
    private boolean pescatarian;
    private boolean vegan;
    private boolean vegetarian;

    //  Basic constructor for setting a users allergy preferences. (6 allegerns)
    public Preferences(boolean allergy_nuts, boolean allergy_eggs, boolean allergy_milk, boolean allergy_shellfish, boolean allergy_soya, boolean allergy_gluten){
        this.allergy_nuts = allergy_nuts;
        this.allergy_eggs = allergy_eggs;
        this.allergy_shellfish = allergy_shellfish;
        this.allergy_soya = allergy_soya;
        this.allergy_milk = allergy_milk;
        this.allergy_gluten = allergy_gluten;
    }

    //  Constructor for setting all of a users preferences.
    public Preferences(boolean allergy_celery,
                       boolean allergy_crustacean, boolean allergy_eggs, boolean allergy_fish,
                       boolean allergy_gluten, boolean allergy_milk, boolean allergy_mustard,
                       boolean allergy_nuts, boolean allergy_peanuts, boolean allergy_sesame,
                       boolean allergy_shellfish, boolean allergy_soya, boolean allergy_sulphide,
                       boolean diabetic, boolean halal, boolean high_protein, boolean kosher,
                       boolean lactose_free, boolean lactovegetarian, boolean low_carb,
                       boolean low_sodium, boolean no_alcohol, boolean no_pork,
                       boolean ovovegetarian, boolean pescatarian, boolean vegan,
                       boolean vegetarian){
        this.allergy_celery = allergy_celery;
        this.allergy_crustacean = allergy_crustacean;
        this.allergy_eggs = allergy_eggs;
        this.allergy_fish = allergy_fish;
        this.allergy_gluten = allergy_gluten;
        this.allergy_milk = allergy_milk;
        this.allergy_mustard = allergy_mustard;
        this.allergy_nuts = allergy_nuts;
        this.allergy_peanuts = allergy_peanuts;
        this.allergy_sesame = allergy_sesame;
        this.allergy_shellfish = allergy_shellfish;
        this.allergy_soya = allergy_soya;
        this.allergy_sulphide = allergy_sulphide;
        this.diabetic = diabetic;
        this.halal = halal;
        this.high_protein = high_protein;
        this.kosher = kosher;
        this.lactose_free = lactose_free;
        this.lactovegetarian = lactovegetarian;
        this.low_carb = low_carb;
        this.low_sodium = low_sodium;
        this.no_alcohol = no_alcohol;
        this.no_pork = no_pork;
        this.ovovegetarian = ovovegetarian;
        this.pescatarian = pescatarian;
        this.vegan = vegan;
        this.vegetarian = vegetarian;
    }

    // Getters and setters for all variables


    public void setAllergy_celery(boolean allergy_celery) {
        this.allergy_celery = allergy_celery;
    }

    public boolean isAllergy_crustacean() {
        return allergy_crustacean;
    }

    public void setAllergy_crustacean(boolean allergy_crustacean) {
        this.allergy_crustacean = allergy_crustacean;
    }

    public boolean isAllergy_eggs() {
        return allergy_eggs;
    }

    public void setAllergy_eggs(boolean allergy_eggs) {
        this.allergy_eggs = allergy_eggs;
    }

    public boolean isAllergy_fish() {
        return allergy_fish;
    }

    public void setAllergy_fish(boolean allergy_fish) {
        this.allergy_fish = allergy_fish;
    }

    public boolean isAllergy_gluten() {
        return allergy_gluten;
    }

    public void setAllergy_gluten(boolean allergy_gluten) {
        this.allergy_gluten = allergy_gluten;
    }

    public boolean isAllergy_milk() {
        return allergy_milk;
    }

    public void setAllergy_milk(boolean allergy_milk) {
        this.allergy_milk = allergy_milk;
    }

    public boolean isAllergy_mustard() {
        return allergy_mustard;
    }

    public void setAllergy_mustard(boolean allergy_mustard) {
        this.allergy_mustard = allergy_mustard;
    }

    public boolean isAllergy_nuts() {
        return allergy_nuts;
    }

    public void setAllergy_nuts(boolean allergy_nuts) {
        this.allergy_nuts = allergy_nuts;
    }

    public boolean isAllergy_peanuts() {
        return allergy_peanuts;
    }

    public void setAllergy_peanuts(boolean allergy_peanuts) {
        this.allergy_peanuts = allergy_peanuts;
    }

    public boolean isAllergy_sesame() {
        return allergy_sesame;
    }

    public void setAllergy_sesame(boolean allergy_sesame) {
        this.allergy_sesame = allergy_sesame;
    }

    public boolean isAllergy_shellfish() {
        return allergy_shellfish;
    }

    public void setAllergy_shellfish(boolean allergy_shellfish) {
        this.allergy_shellfish = allergy_shellfish;
    }

    public boolean isAllergy_soya() {
        return allergy_soya;
    }

    public void setAllergy_soya(boolean allergy_soya) {
        this.allergy_soya = allergy_soya;
    }

    public boolean isAllergy_sulphide() {
        return allergy_sulphide;
    }

    public void setAllergy_sulphide(boolean allergy_sulphide) {
        this.allergy_sulphide = allergy_sulphide;
    }

    public boolean isDiabetic() {
        return diabetic;
    }

    public void setDiabetic(boolean diabetic) {
        this.diabetic = diabetic;
    }

    public boolean isHalal() {
        return halal;
    }

    public void setHalal(boolean halal) {
        this.halal = halal;
    }

    public boolean isHigh_protein() {
        return high_protein;
    }

    public void setHigh_protein(boolean high_protein) {
        this.high_protein = high_protein;
    }

    public boolean isKosher() {
        return kosher;
    }

    public void setKosher(boolean kosher) {
        this.kosher = kosher;
    }

    public boolean isLactose_free() {
        return lactose_free;
    }

    public void setLactose_free(boolean lactose_free) {
        this.lactose_free = lactose_free;
    }

    public boolean isLactovegetarian() {
        return lactovegetarian;
    }

    public void setLactovegetarian(boolean lactovegetarian) {
        this.lactovegetarian = lactovegetarian;
    }

    public boolean isLow_carb() {
        return low_carb;
    }

    public void setLow_carb(boolean low_carb) {
        this.low_carb = low_carb;
    }

    public boolean isLow_sodium() {
        return low_sodium;
    }

    public void setLow_sodium(boolean low_sodium) {
        this.low_sodium = low_sodium;
    }

    public boolean isNo_alcohol() {
        return no_alcohol;
    }

    public void setNo_alcohol(boolean no_alcohol) {
        this.no_alcohol = no_alcohol;
    }

    public boolean isNo_pork() {
        return no_pork;
    }

    public void setNo_pork(boolean no_pork) {
        this.no_pork = no_pork;
    }

    public boolean isOvovegetarian() {
        return ovovegetarian;
    }

    public void setOvovegetarian(boolean ovovegetarian) {
        this.ovovegetarian = ovovegetarian;
    }

    public boolean isPescatarian() {
        return pescatarian;
    }

    public void setPescatarian(boolean pescatarian) {
        this.pescatarian = pescatarian;
    }

    public boolean isVegan() {
        return vegan;
    }

    public void setVegan(boolean vegan) {
        this.vegan = vegan;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        this.vegetarian = vegetarian;
    }

}