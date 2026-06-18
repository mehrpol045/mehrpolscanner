package ui

import (
	"bytes"
	"encoding/json"
	"fmt"
	"net/http"
	"os"
	"strings"
	"time"
)

type telegramPayload struct {
	ChatID string `json:"chat_id"`
	Text   string `json:"text"`
}

func sendTelegramSummary(text string) error {
	token := strings.TrimSpace(os.Getenv("MEHRPOL_TELEGRAM_BOT_TOKEN"))
	chatID := strings.TrimSpace(os.Getenv("MEHRPOL_TELEGRAM_CHAT_ID"))
	if token == "" || chatID == "" {
		return nil
	}
	if len(text) > 3900 {
		text = text[:3900] + "..."
	}
	payload, _ := json.Marshal(telegramPayload{ChatID: chatID, Text: text})
	req, err := http.NewRequest(http.MethodPost, fmt.Sprintf("https://api.telegram.org/bot%s/sendMessage", token), bytes.NewReader(payload))
	if err != nil {
		return err
	}
	req.Header.Set("Content-Type", "application/json")
	client := &http.Client{Timeout: 10 * time.Second}
	resp, err := client.Do(req)
	if err != nil {
		return err
	}
	defer resp.Body.Close()
	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		return fmt.Errorf("telegram returned HTTP %d", resp.StatusCode)
	}
	return nil
}

func telegramScanSummary(historyPath, compare string, healthy, phase2OK int) string {
	return fmt.Sprintf("mehrpol scan complete\nHealthy endpoints: %d\nPhase 2 working: %d\n%s\nHistory: %s", healthy, phase2OK, compare, historyPath)
}
