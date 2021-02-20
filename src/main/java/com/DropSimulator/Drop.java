/*
 * Copyright (c) 2021, Marshall <https://github.com/mxp190009>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.DropSimulator;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Drop {

    int id;
    String quantity;    // quantity is a string because some quantities are an interval, ex. (1-10).
    double rarity;
    int rolls;
    String name;
    boolean tertiary = false;
    boolean catacomb = false;
    boolean preRoll = false;
    boolean wilderness = false;
    boolean unique = false;

    public Drop(int id, String quantity, int rolls, double rarity, String name){

        this.id = id;
        this.quantity = quantity;
        this.rolls = rolls;
        this.rarity = rarity;
        this.name = name;

    }

    public Drop(Drop other){ // allows for the deep copying of a drop with a quantity of 0
        this(other.getId(), "0", other.getRolls(), other.getRarity(), other.getName());
    }

    public String toString(){
        String outputString = id + ":" + name + ":" + quantity + ":" + rarity + ":" + rolls;
        return outputString;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public double getRarity() {
        return rarity;
    }

    public void setRarity(double rarity) {
        this.rarity = rarity;
    }

    public int getRolls() {
        return rolls;
    }

    public void setRolls(int rolls) {
        this.rolls = rolls;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isTertiary() {
        return tertiary;
    }

    public void setTertiary(boolean tertiary) {
        this.tertiary = tertiary;
    }

    public boolean isCatacomb(){
        return catacomb;
    }

    public void setCatacomb(boolean catacomb){
        this.catacomb = catacomb;

    }

    public boolean isPreRoll(){
        return preRoll;
    }

    public void setPreRoll(boolean preRoll){
        this.preRoll = preRoll;
    }

    public boolean isWildernessSlayer(){
        return wilderness;

    }

    public void setWildernessSlayer(boolean wilderness){
        this.wilderness = wilderness;

    }

    public boolean isUnique(){
        return unique;
    }

    public void setUnique(boolean unique){
        this.unique = unique;
    }

    // isSame returns true if 2 drops have the same ID
    public boolean sameID(Drop myDrop){

        boolean sameID = false;

        if(this.getId() == myDrop.getId()){
            sameID = true;
        }

        return sameID;
    }

    /*
     * the determineTertiary method determines if a drop is a tertiary by checking it
     * against the wiki's tertiary drop table.
     */

    public void determineTertiary(Elements table) throws IOException {

        if (table != null && !table.toString().trim().isEmpty()) { // if the npc has a tertiary drop table
            Element myTable = table.parents().first().nextElementSibling();
            Elements contained = myTable.getElementsContainingText(this.name);

            if(contained != null && !contained.toString().trim().isEmpty()) { // if not null and not empty

                this.setTertiary(true);

            }
        }
    }

    /*
     * the determinePreRoll method determines if a drop is a pre roll drop by checking it
     * against the wiki's pre roll drop table. Classifies all pre-rolls and uniques as pre-rolls.
     * Prior to calculating uniques as pre-rolls many bosses has total probabilities > 1.
     * By pre-rolling uniques the simulation is significantly more accurate.
     */

    public void determinePreRoll(Elements table) throws IOException{

        if (table != null && !table.toString().trim().isEmpty()) { // if the npc has a pre roll table
            Element myTable = table.parents().first().nextElementSibling();
            Elements contained = myTable.getElementsContainingText(this.name);

            // Like the catacombs, as an example the alchemical hydra, the pre-roll table shows a header between the
            // title and table.

            if(contained.isEmpty()){
                myTable = table.parents().first().nextElementSibling().nextElementSibling();
                contained = myTable.getElementsContainingText(this.name);
            }

            if(contained != null && !contained.toString().trim().isEmpty()) { // if not null and not empty

                this.setPreRoll(true);

            }
        }
    }

    /*
     * the determineUnique method determines if a drop is a unique drop by checking it
     * against the wiki's unique drop table. Classifies all uniques as pre-rolls.
     * Prior to calculating uniques as pre-rolls many bosses has total probabilities > 1.
     * By pre-rolling uniques the simulation is significantly more accurate.
     */

    public void determineUnique(Elements table) throws IOException{

        if (table != null && !table.toString().trim().isEmpty()) { // if the npc has a unique table
            Element myTable = table.parents().first().nextElementSibling();
            Elements contained = myTable.getElementsContainingText(this.name);

            // Sometimes the unique table has an extra header between the title and table

            if(contained.isEmpty()){
                myTable = table.parents().first().nextElementSibling().nextElementSibling();
                contained = myTable.getElementsContainingText(this.name);
            }

            if(contained != null && !contained.toString().trim().isEmpty()) { // if not null and not empty

                this.setUnique(true);

            }
        }
    }

    /*
     * the determineCatacomb method determines if a drop is a catacomb tertiary drop by checking it
     * against the wiki's catacomb tertiary table.
     */

    public void determineCatacomb(Elements table) throws IOException {

        if (table != null && !table.toString().trim().isEmpty()) { // if the npc has a tertiary drop table
            Element myTable = table.parents().first().nextElementSibling();
            Elements contained = myTable.getElementsContainingText(this.name);

            // The wiki sometimes displays an extra header after the title if the monster has variants that exist
            // both inside and outside of the catacombs. Need to check if contained is empty. If it is empty, then
            // it is possible the extra header was being parsed as an element rather than the table itself. Therefore
            // for all monsters with catacomb drops it is necessary to also check the nextElement AFTER the nextElement
            // because the next element might just be more text.

            if(contained.isEmpty()){
                myTable = table.parents().first().nextElementSibling().nextElementSibling();
                contained = myTable.getElementsContainingText(this.name);
            }

            if(contained != null && !contained.toString().trim().isEmpty()) { // if not null and not empty

                this.setCatacomb(true);

            }
        }
    }

    /*
     * the determineWildernessSlayer method determines if a drop is a wilderness slayer tertiary drop
     * by checking it against the wiki's wilderness slayer tertiary table.
     */

    public void determineWildernessSlayer(Elements table) throws IOException {

        if (table != null && !table.toString().trim().isEmpty()) { // if the npc has a tertiary drop table
            Element myTable = table.parents().first().nextElementSibling();
            Elements contained = myTable.getElementsContainingText(this.name);

            // Like the catacomb drops, the wiki sometimes displays an extra header after the title if the monster has
            // variants that exist both inside and outside of the wilderness. Need to check if contained is empty. If
            // it is empty, then  it is possible the extra header was being parsed as an element rather than the table
            // itself. Therefore for all monsters with catacomb drops it is necessary to also check the nextElement
            // AFTER the nextElement because the next element might just be more text.

            if(contained.isEmpty()){
                myTable = table.parents().first().nextElementSibling().nextElementSibling();
                contained = myTable.getElementsContainingText(this.name);
            }

            if(contained != null && !contained.toString().trim().isEmpty()) { // if not null and not empty

                this.setWildernessSlayer(true);

            }
        }
    }
}
