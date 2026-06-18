package banner

import (
	"strings"

	"github.com/charmbracelet/lipgloss"
)

// Art is the multi-line ASCII art for "mehrpol".
// Uses box-drawing block characters for a bold, retro look.
const Art = `
 ███╗   ███╗███████╗██╗  ██╗██████╗ ██████╗  ██████╗ ██╗     
 ████╗ ████║██╔════╝██║  ██║██╔══██╗██╔══██╗██╔═══██╗██║     
 ██╔████╔██║█████╗  ███████║██████╔╝██████╔╝██║   ██║██║     
 ██║╚██╔╝██║██╔══╝  ██╔══██║██╔══██╗██╔═══╝ ██║   ██║██║     
 ██║ ╚═╝ ██║███████╗██║  ██║██║  ██║██║     ╚██████╔╝███████╗
 ╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝      ╚═════╝ ╚══════╝`

// Tagline is shown beneath the art.
const Tagline = "  Cloudflare connectivity and SNI checks for restricted networks"

// rainbowPalette is a smooth warm→cool gradient used for color cycling.
var rainbowPalette = []string{
	"#006064", "#00838F", "#0097A7", "#00ACC1", "#00BCD4",
	"#26C6DA", "#4DD0E1", "#80DEEA", "#B2EBF2", "#E0F7FA",
}

// Render applies a rainbow gradient to the ASCII art.
// frame controls the color offset for animation — increment it each tick.
func Render(frame int) string {
	lines := strings.Split(Art, "\n")
	var out strings.Builder

	for _, line := range lines {
		runes := []rune(line)
		for col, r := range runes {
			idx := ((col+frame)%len(rainbowPalette) + len(rainbowPalette)) % len(rainbowPalette)
			style := lipgloss.NewStyle().Foreground(lipgloss.Color(rainbowPalette[idx])).Bold(true)
			out.WriteString(style.Render(string(r)))
		}
		out.WriteRune('\n')
	}

	dim := lipgloss.NewStyle().Foreground(lipgloss.Color("#555555")).Italic(true)
	out.WriteString(dim.Render(Tagline))
	out.WriteRune('\n')

	return out.String()
}

// Version returns a static (non-animated) render at frame=0, suitable for
// non-TUI contexts like `--version`.
func RenderStatic() string {
	return Render(0)
}
