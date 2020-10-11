package com.decagon.webscrappinggroupb.dataloader;


import com.decagon.webscrappinggroupb.service.ProductService;
import com.decagon.webscrappinggroupb.service.ScraperImpl.AlikayNaturalsScrapper;
import com.decagon.webscrappinggroupb.util.ScraperRun;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class BootstrapData{


//    private final QuestionRepository questionRepository;
//    private final Environment environment;
    private final ScraperRun scraperRun;

    private final Logger logger = LoggerFactory.getLogger(AlikayNaturalsScrapper.class);
//
//    @Autowired
//    public BootstrapData(QuestionRepository questionRepository, Environment environment) {
//        this.questionRepository = questionRepository;
//        this.environment = environment;
//    }

    @Autowired
    public BootstrapData(ScraperRun scraperRun) {
        this.scraperRun = scraperRun;
    }

    @EventListener
    public void run(ContextRefreshedEvent event) {
        logger.info("running in post construct");
        scraperRun.run();
//      generateQuestions();
    }

//    private void generateQuestions() {
//        Question question1 = new Question();
//        question1.setTitle("Hair Description");
//        question1.setQuestion("How would you describe your hair?");
//        question1.setOptions("Dry, Weak, Damaged, Split, Dull, Frizzy, Coarse, Fine, Healthy");
//
//        Question question2 = new Question();
//        question2.setTitle("Hair Type");
//        question2.setQuestion("What is your Hair Type");
//        question2.setOptions("4c - zig zag pattern / tightest coils, 4b - tight coils, 4a - \"S\" pattern / tight coils, 3c - tight / \"corkscrew\" ringlets, 3b - springy ringlets, 3a - large loose curls, 2c - mix of wavy and curly, 2b - wavy / some volume, 2a - large waves / \"tousled\" look, 1 - straight");
//
//        Question question3 = new Question();
//        question3.setTitle("Hair Dry Time");
//        question3.setQuestion("How long does it take for your hair to air dry");
//        question3.setOptions("0-3, 1-3, 3");
//
//        Question question4 = new Question();
//        question4.setTitle("Others");
//        question4.setQuestion("What Else");
//        question4.setOptions("I have allergies, I'm transitioning to natural hair, I have thin hair, I use a relaxer, I have a tender scalp, I exercise a lot, I have locs, My hair is dyed, I have dandruff, Nothing else, other");
//
//        Question question5 = new Question();
//        question5.setTitle("Allergies");
//        question5.setQuestion("What are you allergic to");
//        question5.setOptions("");
//
//        Question question6 = new Question();
//        question6.setTitle("productBrands");
//        question6.setQuestion("Great! What brands do you currently use? (ex. Shea Moisture, Pantene, Carol's Daughter etc.)");
//        question6.setOptions("");
//
//        Question question7 = new Question();
//        question7.setTitle("products");
//        question7.setQuestion("What products do you often use");
//        question7.setOptions("Shampoo, Conditioner, Leave-in conditioner, Deep conditioner, Hair mask, Growth oil, Other hair oil, Curl / hair cream, Style cream, Edge control, Gel, Relaxer, Other");
//
//        Question question8 = new Question();
//        question8.setTitle("Satisfaction Level");
//        question8.setQuestion("Are you happy with the products you use");
//        question8.setOptions("");
//
//        Question question9 = new Question();
//        question9.setTitle("Disliked Brands");
//        question9.setQuestion("Which brands don't you like");
//        question9.setOptions("");
//
//        Question question10 = new Question();
//        question10.setTitle("Hair Expectations");
//        question10.setQuestion("Almost there! What are your hair goals");
//        question10.setOptions("Growth (length), Strength, Repair chemical/color damages, More moisture, Transition to natural hair, Restore edges, Get rid of dandruff, I don't really have any, other");
//
//        Question question11 = new Question();
//        question11.setTitle("Product Expectations");
//        question11.setQuestion("When it comes to hair products, what's most important to you");
//        question11.setOptions("Cost, Brand Name, Quality, Sustainability (eco-friendly, cruelty free etc), Fragrance / Smell, Black/minority owned, other");
//
//        Question question12 = new Question();
//        question12.setTitle("Hair Styles");
//        question12.setQuestion("What styles do you like to wear");
//        question12.setOptions("Wash n' go, Blow out, Heat styles (press, curl, hot brush etc), Braids, Crochet, Twist out, Updo, Weave, Wig, Other");
//
//        Question question13 = new Question();
//        question13.setTitle("Price Range");
//        question13.setQuestion("Finally, how much do you spend on each hair product");
//        question13.setOptions("0-8, 8-12, 12-16, 17");
//
//        if (!questionRepository.findById(1L).isPresent()) {
//            questionRepository.save(question1);
//            questionRepository.save(question2);
//            questionRepository.save(question3);
//            questionRepository.save(question4);
//            questionRepository.save(question5);
//            questionRepository.save(question6);
//            questionRepository.save(question7);
//            questionRepository.save(question8);
//            questionRepository.save(question9);
//            questionRepository.save(question10);
//            questionRepository.save(question11);
//            questionRepository.save(question12);
//            questionRepository.save(question13);
//        }
//    }
}