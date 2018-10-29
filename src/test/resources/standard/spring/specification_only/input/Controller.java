package standard.spring.empty.input;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Api(tags = "foo")
@RequestMapping("/foo")
public class Controller {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public Object yo() {
		return "Yo";
	}

	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public Object hello(@RequestParam(value = "name", required = false) String name) {
		return "Hello " + name;
	}

	private static class AlohaPayload{
		public String name;
	}

	@Deprecated
	@RequestMapping(value = "/aloha", method = RequestMethod.POST)
	public Object aloha(@RequestBody AlohaPayload payload){
		return "Aloha " + payload.name;
	}

	@ApiOperation(value = "", hidden = true)
	@RequestMapping(value = "/hi", method = RequestMethod.GET)
	public Object hi(@RequestParam(value = "name", required = false) String name) {
		return "Hi " + name;
	}
}