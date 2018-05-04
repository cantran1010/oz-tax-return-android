package au.mccann.oztaxreturn.rest.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import au.mccann.oztaxreturn.model.Attachment;

/**
 * Created by LongBui on 5/3/18.
 */

public class ReviewPrivateResponse {

    @SerializedName("id")
    private int id;
    @SerializedName("insurer")
    private String insurer;
    @SerializedName("membership_no")
    private String membershipNo;
    @SerializedName("premiums_paid")
    private double premiumsPaid;
    @SerializedName("gov_rebate_received")
    private double govRebateReceived;
    @SerializedName("days_covered")
    private int daysCovered;
    private List<Attachment> attachments = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInsurer() {
        return insurer;
    }

    public void setInsurer(String insurer) {
        this.insurer = insurer;
    }

    public String getMembershipNo() {
        return membershipNo;
    }

    public void setMembershipNo(String membershipNo) {
        this.membershipNo = membershipNo;
    }

    public double getPremiumsPaid() {
        return premiumsPaid;
    }

    public void setPremiumsPaid(double premiumsPaid) {
        this.premiumsPaid = premiumsPaid;
    }

    public double getGovRebateReceived() {
        return govRebateReceived;
    }

    public void setGovRebateReceived(double govRebateReceived) {
        this.govRebateReceived = govRebateReceived;
    }

    public int getDaysCovered() {
        return daysCovered;
    }

    public void setDaysCovered(int daysCovered) {
        this.daysCovered = daysCovered;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        return "ReviewPrivateResponse{" +
                "id=" + id +
                ", insurer='" + insurer + '\'' +
                ", membershipNo='" + membershipNo + '\'' +
                ", premiumsPaid=" + premiumsPaid +
                ", govRebateReceived=" + govRebateReceived +
                ", daysCovered=" + daysCovered +
                ", attachments=" + attachments +
                '}';
    }

}
