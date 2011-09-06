package answer

import (
	"io"
	"image"
	"image/png"
	//"fmt"
)

type HashSet struct {
	colors	[]image.Color
}

func (this *HashSet) Check(color image.Color) bool {
	for i := 0; i < len(this.colors); i ++ {
		buf := this.colors[i]
		r1, g1, b1, _ := buf.RGBA()
		r2, g2, b2, _ := color.RGBA()

		if r1 == r2 && g1 == g2 && b1 == b2 {
			return false
		}
	}

	return true
}

func (this *HashSet) Add(color image.Color) {
	this.colors = append(this.colors, color)
}

func (this *HashSet) Count() int {
	return len(this.colors)
}

func CountColor(reader io.Reader) int {
	img, ret := png.Decode(reader)
	if ret != nil {
		return 0
	}

	bounds := img.Bounds()

	set := new(HashSet)
	for y := 0; y < bounds.Max.Y; y ++ {
		for x := 0; x < bounds.Max.X; x ++ {
			color := img.At(x, y)
			if set.Check(color) {
				set.Add(color)
			}
		}
	}
	return set.Count()
}
