package com.HolgersDream.Deckforge.exceptions;

import com.HolgersDream.Deckforge.service.ExceptionLoggerService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final ExceptionLoggerService loggerService;

    public GlobalExceptionHandler(ExceptionLoggerService loggerService){
        this.loggerService = loggerService;
    }

    @ExceptionHandler(DatabaseConnectionException.class)
    public String handleDatabaseConnection(DatabaseConnectionException dce, Model model){
        loggerService.checkSaveExceptionMessage(dce, rootCauseMessage(dce));
        model.addAttribute("type", "Database fejl");
        model.addAttribute("errorMessage", dce.getMessage());
        return "error";
    }

    @ExceptionHandler(DataAccessException.class)
    public String handleDataAccess(DataAccessException dae, Model model){
        loggerService.checkSaveExceptionMessage(dae, rootCauseMessage(dae));
        model.addAttribute("type", "Data-adgangs fejl");
        model.addAttribute("errorMessage", dae.getMessage());
        return "error";
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public String handleNotAuthorized(NotAuthorizedException nae, Model model){
        model.addAttribute("type", "Adgangs-fejl");
        model.addAttribute("errorMessage", nae.getMessage());
        return "error";
    }

    @ExceptionHandler(NoEventFoundException.class)
    public String handleNoEventFound(NoEventFoundException nefe, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("responseMessage", nefe.getMessage());
        return "redirect:/event/base";
    }

    @ExceptionHandler(ParticipateEventException.class)
    public String handleParticipateEvent(ParticipateEventException pee, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("responseMessage", pee.getMessage());
        return "redirect:/event/base";
    }

    @ExceptionHandler(NoCollectionCardFoundException.class)
    public String handleNoCardFound(NoCollectionCardFoundException ncfe, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("responseMessage", ncfe.getMessage());
        return "redirect:/collection/search";
    }

    @ExceptionHandler(NoDeckFoundException.class)
    public String handleNoDeckFound(NoDeckFoundException ndfe, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("responseMessage", ndfe.getMessage());
        return "redirect:/deck/personal-decks";
    }

    @ExceptionHandler(AddNonexistentDeckCardException.class)
    public String handleAddNonexistentDeckCard(AddNonexistentDeckCardException andce, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("responseMessage", andce.getMessage());
        return "redirect:/deck/personal-decks";
    }

    @ExceptionHandler(DeckCardNotFoundException.class)
    public String handleRemoveNonexistentDeckCard(DeckCardNotFoundException dcnfe, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("responseMessage", dcnfe.getMessage());
        return "redirect:/deck/personal-decks";
    }


    @ExceptionHandler(DeckAccessException.class)
    public String handleDeckAccess(DeckAccessException dae, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("responseMessage", dae.getMessage());
        return "redirect:/deck/personal-decks";
    }

    @ExceptionHandler(DeckCardValidationException.class)
    public String handleDeckCardValidation(DeckCardValidationException dcve, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("responseMessage", dcve.getMessage());
        return "redirect:/deck/personal-decks";
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalStatement(IllegalArgumentException iae, Model model){
        model.addAttribute("type", "Ugyldigt Input");
        model.addAttribute("errorMessage", iae.getMessage());
        return "error";
    }

    @ExceptionHandler(RegisterValidationException.class)
    public String handleRegisterValidation(RegisterValidationException rve, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("responseMessage", rve.getMessage());
        return "redirect:/authentication/register";
    }

    @ExceptionHandler(LoginValidationException.class)
    public String handleLoginValidation(LoginValidationException lve, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("responseMessage", lve.getMessage());
        return "redirect:/authentication/login";
    }

    @ExceptionHandler(DeckValidationException.class)
    public String handleNewDeckValidation(DeckValidationException ndve, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("responseMessage", ndve.getMessage());
        return "redirect:/deck/add-deck";
    }

    @ExceptionHandler(EventValidationException.class)
    public String handleEventValidation(EventValidationException eve, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("responseMessage", eve.getMessage());
        return "redirect:/event/add-event";
    }


    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFound(NoResourceFoundException nrfe, Model model){
        model.addAttribute("type", "Ukendt side");
        model.addAttribute("errorMessage", "Siden eller resourcen du prøvede at få fat på virker ikke til at eksistere");

        return "error";
    }



    @ExceptionHandler(Exception.class)
    public String handleGeneric(Exception ex, Model model){
        loggerService.checkSaveExceptionMessage(ex, rootCauseMessage(ex));
        model.addAttribute("type", "Ukendt fejl");
        model.addAttribute("errorMessage", "Det er ukendt hvad der gik galt");
        return "error";
    }

    private String rootCauseMessage(Throwable t){
        Throwable root = t;
        while (root.getCause() != null){
            root = root.getCause();
        }
        return root.getClass().getSimpleName() + " : " + root.getMessage();
    }


}
