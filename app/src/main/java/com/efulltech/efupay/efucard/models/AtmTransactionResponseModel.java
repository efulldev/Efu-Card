package com.efulltech.efupay.efucard.models;

import java.io.Serializable;


public class AtmTransactionResponseModel implements Serializable {
    private int id;
    private String batchno;
    private String seqno;
    private String stan;
    private String terminalid;
    private String marchantid;
    private int fromac;
    private int toac;
    private int transtype;
    private String transname;
    private String pan;
    private String amount;
    private String cashback;
    private String expiry;
    private String mcc;
    private String iccdata;
    private String panseqno;
    private String bin;
    private String track1;
    private String track2;
    private String track3;
    private String refno;
    private String servicecode;
    private String marchant;
    private String currencycode;
    private String pinblock;
    private String mti;
    private String datetime;
    private String samount;
    private String tfee;
    private String sfee;
    private String aid;
    private String cardholdername;
    private String label;
    private String tvr;
    private String tsi;
    private String ac;
    private String date;
    private String time;
    private String status;
    private String responsecode;
    private String responsemessage;
    private String authid;
    private int mode;
    private String balance;
    private int deleted;
    private String institutionid;
    private String localtime;
    private String localdate;
    private boolean print;

    public AtmTransactionResponseModel(String batchno, String seqno, String stan, String terminalid, String marchantid, int fromac, int toac, int transtype, String transname, String pan, String amount, String cashback, String expiry, String mcc, String iccdata, String panseqno, String bin, String track1, String track2, String track3, String refno, String servicecode, String marchant, String currencycode, String pinblock, String mti, String datetime, String samount, String tfee, String sfee, String aid, String cardholdername, String label, String tvr, String tsi, String ac, String date, String time, String status, String responsecode, String responsemessage, String authid, int mode, String balance, int deleted, String institutionid, String localtime, String localdate) {
        this.batchno = batchno;
        this.seqno = seqno;
        this.stan = stan;
        this.terminalid = terminalid;
        this.marchantid = marchantid;
        this.fromac = fromac;
        this.toac = toac;
        this.transtype = transtype;
        this.transname = transname;
        this.pan = pan;
        this.amount = amount;
        this.cashback = cashback;
        this.expiry = expiry;
        this.mcc = mcc;
        this.iccdata = iccdata;
        this.panseqno = panseqno;
        this.bin = bin;
        this.track1 = track1;
        this.track2 = track2;
        this.track3 = track3;
        this.refno = refno;
        this.servicecode = servicecode;
        this.marchant = marchant;
        this.currencycode = currencycode;
        this.pinblock = pinblock;
        this.mti = mti;
        this.datetime = datetime;
        this.samount = samount;
        this.tfee = tfee;
        this.sfee = sfee;
        this.aid = aid;
        this.cardholdername = cardholdername;
        this.label = label;
        this.tvr = tvr;
        this.tsi = tsi;
        this.ac = ac;
        this.date = date;
        this.time = time;
        this.status = status;
        this.responsecode = responsecode;
        this.responsemessage = responsemessage;
        this.authid = authid;
        this.mode = mode;
        this.balance = balance;
        this.deleted = deleted;
        this.institutionid = institutionid;
        this.localtime = localtime;
        this.localdate = localdate;
    }

    public AtmTransactionResponseModel(int id, String batchno, String seqno, String stan, String terminalid, String marchantid, int fromac, int toac, int transtype, String transname, String pan, String amount, String cashback, String expiry, String mcc, String iccdata, String panseqno, String bin, String track1, String track2, String track3, String refno, String servicecode, String marchant, String currencycode, String pinblock, String mti, String datetime, String samount, String tfee, String sfee, String aid, String cardholdername, String label, String tvr, String tsi, String ac, String date, String time, String status, String responsecode, String responsemessage, String authid, int mode, String balance, int deleted, String institutionid, String localtime, String localdate) {
        this.id = id;
        this.batchno = batchno;
        this.seqno = seqno;
        this.stan = stan;
        this.terminalid = terminalid;
        this.marchantid = marchantid;
        this.fromac = fromac;
        this.toac = toac;
        this.transtype = transtype;
        this.transname = transname;
        this.pan = pan;
        this.amount = amount;
        this.cashback = cashback;
        this.expiry = expiry;
        this.mcc = mcc;
        this.iccdata = iccdata;
        this.panseqno = panseqno;
        this.bin = bin;
        this.track1 = track1;
        this.track2 = track2;
        this.track3 = track3;
        this.refno = refno;
        this.servicecode = servicecode;
        this.marchant = marchant;
        this.currencycode = currencycode;
        this.pinblock = pinblock;
        this.mti = mti;
        this.datetime = datetime;
        this.samount = samount;
        this.tfee = tfee;
        this.sfee = sfee;
        this.aid = aid;
        this.cardholdername = cardholdername;
        this.label = label;
        this.tvr = tvr;
        this.tsi = tsi;
        this.ac = ac;
        this.date = date;
        this.time = time;
        this.status = status;
        this.responsecode = responsecode;
        this.responsemessage = responsemessage;
        this.authid = authid;
        this.mode = mode;
        this.balance = balance;
        this.deleted = deleted;
        this.institutionid = institutionid;
        this.localtime = localtime;
        this.localdate = localdate;
    }

    public boolean isPrint() {
        return this.print;
    }

    public void setPrint(boolean print) {
        this.print = print;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBatchno() {
        return this.batchno;
    }

    public void setBatchno(String batchno) {
        this.batchno = batchno;
    }

    public String getSeqno() {
        return this.seqno;
    }

    public void setSeqno(String seqno) {
        this.seqno = seqno;
    }

    public String getStan() {
        return this.stan;
    }

    public void setStan(String stan) {
        this.stan = stan;
    }

    public String getTerminalid() {
        return this.terminalid;
    }

    public void setTerminalid(String terminalid) {
        this.terminalid = terminalid;
    }

    public String getMarchantid() {
        return this.marchantid;
    }

    public void setMarchantid(String marchantid) {
        this.marchantid = marchantid;
    }

    public int getFromac() {
        return this.fromac;
    }

    public void setFromac(int fromac) {
        this.fromac = fromac;
    }

    public int getToac() {
        return this.toac;
    }

    public void setToac(int toac) {
        this.toac = toac;
    }

    public int getTranstype() {
        return this.transtype;
    }

    public void setTranstype(int transtype) {
        this.transtype = transtype;
    }

    public String getTransname() {
        return this.transname;
    }

    public void setTransname(String transname) {
        this.transname = transname;
    }

    public String getPan() {
        return this.pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getAmount() {
        return this.amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCashback() {
        return this.cashback;
    }

    public void setCashback(String cashback) {
        this.cashback = cashback;
    }

    public String getExpiry() {
        return this.expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getMcc() {
        return this.mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getIccdata() {
        return this.iccdata;
    }

    public void setIccdata(String iccdata) {
        this.iccdata = iccdata;
    }

    public String getPanseqno() {
        return this.panseqno;
    }

    public void setPanseqno(String panseqno) {
        this.panseqno = panseqno;
    }

    public String getBin() {
        return this.bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public String getTrack1() {
        return this.track1;
    }

    public void setTrack1(String track1) {
        this.track1 = track1;
    }

    public String getTrack2() {
        return this.track2;
    }

    public void setTrack2(String track2) {
        this.track2 = track2;
    }

    public String getTrack3() {
        return this.track3;
    }

    public void setTrack3(String track3) {
        this.track3 = track3;
    }

    public String getRefno() {
        return this.refno;
    }

    public void setRefno(String refno) {
        this.refno = refno;
    }

    public String getServicecode() {
        return this.servicecode;
    }

    public void setServicecode(String servicecode) {
        this.servicecode = servicecode;
    }

    public String getMarchant() {
        return this.marchant;
    }

    public void setMarchant(String marchant) {
        this.marchant = marchant;
    }

    public String getCurrencycode() {
        return this.currencycode;
    }

    public void setCurrencycode(String currencycode) {
        this.currencycode = currencycode;
    }

    public String getPinblock() {
        return this.pinblock;
    }

    public void setPinblock(String pinblock) {
        this.pinblock = pinblock;
    }

    public String getMti() {
        return this.mti;
    }

    public void setMti(String mti) {
        this.mti = mti;
    }

    public String getDatetime() {
        return this.datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getSamount() {
        return this.samount;
    }

    public void setSamount(String samount) {
        this.samount = samount;
    }

    public String getTfee() {
        return this.tfee;
    }

    public void setTfee(String tfee) {
        this.tfee = tfee;
    }

    public String getSfee() {
        return this.sfee;
    }

    public void setSfee(String sfee) {
        this.sfee = sfee;
    }

    public String getAid() {
        return this.aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getCardholdername() {
        return this.cardholdername;
    }

    public void setCardholdername(String cardholdername) {
        this.cardholdername = cardholdername;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTvr() {
        return this.tvr;
    }

    public void setTvr(String tvr) {
        this.tvr = tvr;
    }

    public String getTsi() {
        return this.tsi;
    }

    public void setTsi(String tsi) {
        this.tsi = tsi;
    }

    public String getAc() {
        return this.ac;
    }

    public void setAc(String ac) {
        this.ac = ac;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponsecode() {
        return this.responsecode;
    }

    public void setResponsecode(String responsecode) {
        this.responsecode = responsecode;
    }

    public String getResponsemessage() {
        return this.responsemessage;
    }

    public void setResponsemessage(String responsemessage) {
        this.responsemessage = responsemessage;
    }

    public String getAuthid() {
        return this.authid;
    }

    public void setAuthid(String authid) {
        this.authid = authid;
    }

    public int getMode() {
        return this.mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getBalance() {
        return this.balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public int getDeleted() {
        return this.deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public String getInstitutionid() {
        return this.institutionid;
    }

    public void setInstitutionid(String institutionid) {
        this.institutionid = institutionid;
    }

    public String getLocaltime() {
        return this.localtime;
    }

    public void setLocaltime(String localtime) {
        this.localtime = localtime;
    }

    public String getLocaldate() {
        return this.localdate;
    }

    public void setLocaldate(String localdate) {
        this.localdate = localdate;
    }
}
