package com.shiyunzhang.wetrade.DataClass;

public class UserInfo {

    private String firstName;
    private String lastName;
    private String email;
    private String college;
    private String expectedGraduactionDate;
    private String address;
    private String city;
    private String state;
    private long zipCode;
    private String gender;
    private String id;

    public UserInfo() {
        //public no-arg constructor needed
    }

    public UserInfo(String firstName, String lastName, String email, String college, String expectedGraduactionDate, String address, String city, String state, long zipCode, String gender, String id) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.college = college;
        this.expectedGraduactionDate = expectedGraduactionDate;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.gender = gender;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getExpectedGraduactionDate() {
        return expectedGraduactionDate;
    }

    public void setExpectedGraduactionDate(String expectedGraduactionDate) {
        this.expectedGraduactionDate = expectedGraduactionDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getZipCode() {
        return zipCode;
    }

    public void setZipCode(long zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
