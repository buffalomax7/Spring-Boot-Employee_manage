package backend.hobbiebackend.web;


import backend.hobbiebackend.model.entities.Test;
import backend.hobbiebackend.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class TestController {

    private final TestService testService;


    @Autowired
    public TestController(TestService testService) {
        this.testService = testService;
    }




    @PostMapping("/test")
    public ResponseEntity<HttpStatus> saveTestResults(@RequestBody Test results) {
        this.testService.saveTestResults(results);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
