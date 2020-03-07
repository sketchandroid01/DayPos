package com.daypos.localdb;

public class PrinterData {

    private int id;
    private String printer_name;
    private String location_path_name;
    private String location_path;
    private String is_print;
    private String paper_width;

    public int getId() {
        return id;
    }

    public PrinterData setId(int id) {
        this.id = id;
        return this;
    }

    public String getPrinter_name() {
        return printer_name;
    }

    public PrinterData setPrinter_name(String printer_name) {
        this.printer_name = printer_name;
        return this;
    }

    public String getLocation_path_name() {
        return location_path_name;
    }

    public PrinterData setLocation_path_name(String location_path_name) {
        this.location_path_name = location_path_name;
        return this;
    }

    public String getLocation_path() {
        return location_path;
    }

    public PrinterData setLocation_path(String location_path) {
        this.location_path = location_path;
        return this;
    }

    public String getIs_print() {
        return is_print;
    }

    public PrinterData setIs_print(String is_print) {
        this.is_print = is_print;
        return this;
    }

    public String getPaper_width() {
        return paper_width;
    }

    public PrinterData setPaper_width(String paper_width) {
        this.paper_width = paper_width;
        return this;
    }
}
