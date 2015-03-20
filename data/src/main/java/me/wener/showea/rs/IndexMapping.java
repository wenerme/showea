package me.wener.showea.rs;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("hi")
@Controller
public class IndexMapping
{

    @RequestMapping(method = GET)
    public String get()
    {
        return "Say hi to wener.";
    }
}
