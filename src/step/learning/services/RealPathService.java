package step.learning.services;

import com.google.inject.Singleton;

import javax.servlet.http.HttpServletRequest;

@Singleton
public class RealPathService {
    private String realPath = "";
    public void setRealPath(String realPath){
        if(this.realPath.equals(""))
            this.realPath = realPath;
    }
    public String getRealPath(){
        return realPath;
    }
}
