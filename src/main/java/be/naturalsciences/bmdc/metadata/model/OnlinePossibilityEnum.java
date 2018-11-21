/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.model;

/**
 *
 * @author thomas
 */
//toString() shoudl fit the sentence: "service providing " xxx
public enum OnlinePossibilityEnum {
    DOWNLOAD {
        public String toString() {
            return "to download";
        }
    },
    INFORMATION {
        public String toString() {
            return "to view information on";
        }
    },
    OFFLINE_ACCESS {
        public String toString() {
            return "to access offline";
        }
    },
    ORDER {
        public String toString() {
            return "the possibility to order";
        }
    },
    SEARCH {
        public String toString() {
            return "to search for";
        }
    },
    MAP {
        public String toString() {
            return "to view a map of";
        }
    }

}
