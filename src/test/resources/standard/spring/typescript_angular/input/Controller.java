package standard.spring.empty.input;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/foo")
public class Controller {

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public Object hello(){
        return "Hello World";
    }

}