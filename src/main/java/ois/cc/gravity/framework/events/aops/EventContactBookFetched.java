package ois.cc.gravity.framework.events.aops;

import code.ua.events.EventOK;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.EventCode;
import ois.cc.gravity.objects.OContactBook;

import java.util.ArrayList;

public class EventContactBookFetched extends EventOK {

     private ArrayList<OContactBook> OContactBooks =new ArrayList<>();
    private Integer Offset;
    private Integer Limit;
    private Integer RecordCount;
    public EventContactBookFetched(Request request) {
        super(request, EventCode.ContactBook);
    }

    public ArrayList<OContactBook> getoContactBooks() {
        return OContactBooks;
    }

    public void setoContactBooks(ArrayList<OContactBook> oContactBooks) {
        this.OContactBooks = oContactBooks;
    }

    public ArrayList<OContactBook> getOContactBooks() {
        return OContactBooks;
    }

    public void setOContactBooks(ArrayList<OContactBook> OContactBooks) {
        this.OContactBooks = OContactBooks;
    }

    public Integer getOffset() {
        return Offset;
    }

    public void setOffset(Integer offset) {
        Offset = offset;
    }

    public Integer getLimit() {
        return Limit;
    }

    public void setLimit(Integer limit) {
        Limit = limit;
    }

    public Integer getRecordCount() {
        return RecordCount;
    }

    public void setRecordCount(Integer recordCount) {
        RecordCount = recordCount;
    }
}
