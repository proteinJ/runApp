package com.running.runapp.global.deploy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
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
                pb.redirectErrorStream(true);
                Process process = pb.start();
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    log.info("Deploy script completed successfully");
                } else {
                    log.error("Deploy script failed with exit code: {}", exitCode);
                }
            } catch (Exception e) {
                log.error("Deploy script execution failed", e);
            }
        }).start();

        return ResponseEntity.ok("Deploying...");
    }
}
