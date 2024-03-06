package com.park.demo;

import com.park.demo.another.CallMeInPatch;

public class Need2Fix {

    public String fixme() {
        return "im stupid.";
    }

    public String fixed() {
        return "im fixed. lucky code:" + CallMeInPatch.getMyLuckCode() + "\nprivate method:" + callInPackageMethod();
    }

    private String callInPackageMethod() {
        return "success";
    }

}
