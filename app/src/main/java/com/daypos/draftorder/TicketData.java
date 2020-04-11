package com.daypos.draftorder;

import com.daypos.modifier.ModifierItemsData;

import java.io.Serializable;

public class TicketData implements Serializable {

    private String ticket_id;
    private String ticket_name;
    private String total_quantity;
    private String total_amount;
    private String comment;
    private String created_at;


    public String getTicket_id() {
        return ticket_id;
    }

    public TicketData setTicket_id(String ticket_id) {
        this.ticket_id = ticket_id;
        return this;
    }

    public String getTicket_name() {
        return ticket_name;
    }

    public TicketData setTicket_name(String ticket_name) {
        this.ticket_name = ticket_name;
        return this;
    }

    public String getTotal_quantity() {
        return total_quantity;
    }

    public TicketData setTotal_quantity(String total_quantity) {
        this.total_quantity = total_quantity;
        return this;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public TicketData setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public TicketData setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public String getCreated_at() {
        return created_at;
    }

    public TicketData setCreated_at(String created_at) {
        this.created_at = created_at;
        return this;
    }
}
