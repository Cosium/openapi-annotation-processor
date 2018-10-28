package standard.spring.empty.input;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Api(tags = "foo")
@RequestMapping("/foo")
public class Controller {

	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public Object hello(@RequestParam(value = "name", required = false) String name) {
		return "Hello " + name;
	}

}