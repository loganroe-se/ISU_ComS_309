package CyStaff.app.Messaging;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api(value = "Message Controller", description = "REST APIs related to Message Entity")
@RestController
public class MessageController {

    @Autowired
    MessageRepository messageRepository;

    @ApiOperation(value = "Gets list of all destUserNames who had a chat history" +
                          "with userName in the database.")
    @ApiResponse(code = 200, message = "Returns list of all destUserNames.")
    @GetMapping(path = "/message/destUserNames")
    List<String> getAlldestUserNames(
            @ApiParam(name = "userName", value = "The userName of the user")
            @RequestParam(value = "userName") String userName)
    {
        List <Message> messages = messageRepository.findAllByuserName(userName);
        List <String> destUserNames = new ArrayList<>();

        for (Message message : messages)
        {
            if(!destUserNames.contains(message.getDestUserName()))
            {
                destUserNames.add(message.getDestUserName());
            }
        }

        return destUserNames;
    }
}
