package codesquad.web;

import codesquad.CannotShowException;
import codesquad.domain.Comment;
import codesquad.domain.Milestone;
import codesquad.domain.User;
import codesquad.dto.IssueDto;
import codesquad.security.LoginUser;
import codesquad.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Controller
@RequestMapping("/issues")
public class IssueController {
    private static final Logger log = LoggerFactory.getLogger(IssueController.class);

    @Resource(name = "issueService")
    private IssueService issueService;

    @Resource(name = "milestoneService")
    private MilestoneService milestoneService;

    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "labelService")
    private LabelService labelService;

    @Resource(name = "commentService")
    private CommentService commentService;

    @GetMapping("/form")
    public String createForm(@LoginUser User user) {
        log.debug("issue form");
        return "/issue/form";
    }

    @PostMapping()
    public String create(@LoginUser User user, IssueDto issueDto) {
        log.debug("issue : {}", issueDto.toString());
        issueService.create(user, issueDto);
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable long id, Model model) throws CannotShowException {
        model.addAttribute("issue", issueService.findById(id));
        model.addAttribute("milestones", milestoneService.findAll());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("labels", labelService.findAll());
        model.addAttribute("comments", commentService.findAllByIssueId(id));
        return "/issue/show";
    }

    @PostMapping("/{id}/form")
    String updateForm(@LoginUser User user, @PathVariable long id, Model model) throws CannotShowException {
        model.addAttribute("issue", issueService.findById(id));
        return "/issue/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User user, @PathVariable long id, IssueDto updateIssueDto) {
        issueService.update(id, user, updateIssueDto);
        return String.format("redirect:/issues/%d", id);
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User user, @PathVariable long id) {
        issueService.delete(id);
        return "redirect:/";
    }

    @GetMapping("/{issueId}/milestones/{milestoneId}")
    public String setMilestone(@LoginUser User user, @PathVariable Long issueId, @PathVariable Long milestoneId) {
        issueService.setMilestone(issueId, milestoneId);
        return "redirect:/";
    }

    @GetMapping("/{issueId}/users/{userId}")
    public String setAssignee(@LoginUser User user, @PathVariable Long issueId, @PathVariable Long userId) {
        issueService.setAssignee(issueId, userId);
        return "redirect:/";
    }

    @GetMapping("/{issueId}/labels/{labelId}")
    public String setLabel(@LoginUser User user, @PathVariable Long issueId, @PathVariable Long labelId) {
        issueService.setLabel(issueId, labelId);
        return "redirect:/";
    }

    @PostMapping("/{issueId}/comments")
    public String createComment(@LoginUser User writer, @PathVariable Long issueId, Comment comment) {
        commentService.create(writer, comment);
        return "redirect:/issues/"+issueId;
    }
}
