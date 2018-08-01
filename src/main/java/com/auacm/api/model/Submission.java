package com.auacm.api.model;

import org.springframework.web.multipart.MultipartFile;

public class Submission {
    private MultipartFile file;
    private Long pid;
    private String lang;

    public MultipartFile getFile()  {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
