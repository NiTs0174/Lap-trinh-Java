package com.demo.phone_shop.controller;

import com.demo.phone_shop.model.CartItem;
import com.demo.phone_shop.model.Order;
import com.demo.phone_shop.model.OrderDetail;
import com.demo.phone_shop.model.User;
import com.demo.phone_shop.repository.OrderDetailRepository;
import com.demo.phone_shop.repository.UserRepository;
import com.demo.phone_shop.service.OrderService;
import com.demo.phone_shop.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @Autowired
    private OrderService orderService;

    @GetMapping("/login")
    public String login() {
        return "users/login";
    }

    @GetMapping("/register")
    public String register(@NotNull Model model) {
        model.addAttribute("user", new User()); // Thêm một đối tượng User mới vào model
        return "users/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, // Validate đối tượng User
                           @NotNull BindingResult bindingResult, // Kết quả của quá trình validate
                           Model model) {
        if (bindingResult.hasErrors()) { // Kiểm tra nếu có lỗi validate
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "users/register"; // Trả về lại view "register" nếu có lỗi
        }
        userService.save(user); // Lưu người dùng vào cơ sở dữ liệu
        userService.setDefaultRole(user.getUsername()); // Gán vai trò mặc định cho người dùng
        return "redirect:/login"; // Chuyển hướng người dùng tới trang "login"
    }

    @GetMapping("/manager")
    public String listUsers(Model model) {
        List<User> allUsers = userService.findAll();
        List<User> users = allUsers.stream()
                .filter(user -> user.getRoles().stream().noneMatch(role -> role.getName().equals("MASTER")))
                .collect(Collectors.toList());
        model.addAttribute("users", users);
        return "users/manager";
    }

    @PostMapping("/manager/{id}/role")
    public String updateUserRole(@PathVariable Long id, @RequestParam String roleName) {
        User user = userService.findById(id);
        userService.updateRole(user, roleName);
        return "redirect:/manager";
    }

    //---------------------------
    @GetMapping("/update")
    public String showUpdateUserForm(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("user", user);
        return "/users/update-user";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute User userUpdateRequest, RedirectAttributes redirectAttributes) {
        Long userId = userService.getCurrentUser().getId();
        userService.updateUserInfo(userId, userUpdateRequest.getFullName(), userUpdateRequest.getAddress(), userUpdateRequest.getDateOfBirth(), userUpdateRequest.getGender(), userUpdateRequest.getPhone());

        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thành công.");
        return "redirect:/update";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm() {
        return "/users/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute User changePasswordRequest, RedirectAttributes redirectAttributes) {
        Long userId = userService.getCurrentUser().getId();
        userService.changeUserPassword(userId, changePasswordRequest.getPassword());
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thành công.");
        return "redirect:/change-password";
    }

    @GetMapping("/profile-receipt")
    public String receipt(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        //List<Order> myCourses = orderService.getCoursesByUser(user);
        List<Order> myReceipt = orderService.getCoursesByUser(user);
        model.addAttribute("myReceipt", myReceipt);
        return "/users/profile-receipt";
    }

    @GetMapping("/receipt-details/{orderId}")
    public String showOrderDetail(@PathVariable Long orderId, Model model) {
        Order order = orderService.getOrderById(orderId);
        List<OrderDetail> orderDetails = order.getOrderDetails();

        model.addAttribute("order", order);
        model.addAttribute("orderDetails", orderDetails);

        return "/users/receipt-details";
    }
}
