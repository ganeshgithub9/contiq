package com.ganesh.contiq.exception;

public class FileAccessDeniedException extends RuntimeException{
    public FileAccessDeniedException(String msg){
        super(msg);
    }
}
