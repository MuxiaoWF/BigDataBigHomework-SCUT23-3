package com.muxiao.system;

// 数据模型类
public class RowData {
    public static class StudentsRowData extends RowData {
        private String student_id;
        private String name;
        private String gender;
        private String major;
        private String clazz;
        private String contact;
        private String email;

        public String getStudent_id() {
            return student_id;
        }

        public void setStudent_id(String student_id) {
            this.student_id = student_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getMajor() {
            return major;
        }

        public void setMajor(String major) {
            this.major = major;
        }

        public String getClazz() {
            return clazz;
        }

        public void setClazz(String clazz) {
            this.clazz = clazz;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

    }

    public static class DormitoryRowData extends RowData {
        private Integer dormitory_id;
        private String building_name;
        private Integer room_number;
        private Integer capacity;
        private String status;

        public Integer getDormitory_id() {
            return dormitory_id;
        }

        public void setDormitory_id(Integer dormitory_id) {
            this.dormitory_id = dormitory_id;
        }

        public String getBuilding_name() {
            return building_name;
        }

        public void setBuilding_name(String building_name) {
            this.building_name = building_name;
        }

        public Integer getRoom_number() {
            return room_number;
        }

        public void setRoom_number(Integer room_number) {
            this.room_number = room_number;
        }

        public Integer getCapacity() {
            return capacity;
        }

        public void setCapacity(Integer capacity) {
            this.capacity = capacity;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class ResidenceRowData extends RowData {
        private Integer residence_id;
        private String student_id;
        private Integer dormitory_id;
        private String move_in_date;
        private String move_out_date;

        public Integer getResidence_id() {
            return residence_id;
        }

        public void setResidence_id(Integer residence_id) {
            this.residence_id = residence_id;
        }

        public String getMove_in_date() {
            return move_in_date;
        }

        public void setMove_in_date(String move_in_date) {
            this.move_in_date = move_in_date;
        }

        public String getMove_out_date() {
            return move_out_date;
        }

        public void setMove_out_date(String move_out_date) {
            this.move_out_date = move_out_date;
        }

        public String getStudent_id() {
            return student_id;
        }

        public void setStudent_id(String student_id) {
            this.student_id = student_id;
        }

        public Integer getDormitory_id() {
            return dormitory_id;
        }

        public void setDormitory_id(Integer dormitory_id) {
            this.dormitory_id = dormitory_id;
        }
    }

    public static class RepairRowData extends RowData {
        private Integer repair_id;
        private String student_id;
        private Integer dormitory_id;
        private String request_date;
        private String description;
        private String status;

        public Integer getRepair_id() {
            return repair_id;
        }

        public void setRepair_id(Integer repair_id) {
            this.repair_id = repair_id;
        }

        public String getRequest_date() {
            return request_date;
        }

        public void setRequest_date(String request_date) {
            this.request_date = request_date;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getStudent_id() {
            return student_id;
        }

        public void setStudent_id(String student_id) {
            this.student_id = student_id;
        }

        public Integer getDormitory_id() {
            return dormitory_id;
        }

        public void setDormitory_id(Integer dormitory_id) {
            this.dormitory_id = dormitory_id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class ViolationRowData extends RowData {
        private Integer violation_id;
        private String student_id;
        private Integer dormitory_id;
        private String date;
        private String type;
        private String details;

        public Integer getViolation_id() {
            return violation_id;
        }

        public void setViolation_id(Integer violation_id) {
            this.violation_id = violation_id;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }

        public String getStudent_id() {
            return student_id;
        }

        public void setStudent_id(String student_id) {
            this.student_id = student_id;
        }

        public Integer getDormitory_id() {
            return dormitory_id;
        }

        public void setDormitory_id(Integer dormitory_id) {
            this.dormitory_id = dormitory_id;
        }
    }

    public static class FeeRowData extends RowData {
        private String fee_id;
        private String student_id;
        private String amount;
        private String payment_date;
        private String description;

        public String getFee_id() {
            return fee_id;
        }

        public void setFee_id(String fee_id) {
            this.fee_id = fee_id;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getPayment_date() {
            return payment_date;
        }

        public void setPayment_date(String payment_date) {
            this.payment_date = payment_date;
        }

        public String getStudent_id() {
            return student_id;
        }

        public void setStudent_id(String student_id) {
            this.student_id = student_id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
