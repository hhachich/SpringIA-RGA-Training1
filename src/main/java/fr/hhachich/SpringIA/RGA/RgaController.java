package fr.hhachich.SpringIA.RGA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RgaController {
    private final RagRestController ragRestController;

    @Autowired
    public RgaController(RagRestController ragRestController) {
        this.ragRestController = ragRestController;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/askQuestion")
    public String ask(@RequestParam("question") String question, Model model) {
        String response = ragRestController.ask(question);
        model.addAttribute("response", response);
        return "index";
    }
}