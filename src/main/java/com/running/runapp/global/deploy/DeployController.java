package com.running.runapp.global.deploy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Profile("local")
public class DeployController {

    @Value("${DEPLOY_SECRET}")
    private String deploySecret;

    @PostMapping("/deploy")
    public ResponseEntity<String> deploy(
            @RequestHeader("X-Deploy-Secret") String secret) {

        if (!deploySecret.equals(secret)) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        new Thread(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder(
                        "powershell.exe",
                        "-ExecutionPolicy", "Bypass",
                        "-File", "C:\\Users\\sshuser\\IdeaProjects\\runApp\\deploy.ps1"
                );
                pb.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        return ResponseEntity.ok("Deploying...");
    }
}