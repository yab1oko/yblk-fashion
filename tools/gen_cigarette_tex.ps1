# Animate the user's hand-drawn smoke wisps (same style). UV band y16..28 = texOffs(0,4) h=3.
Add-Type -AssemblyName System.Drawing
$src = "C:\Users\r1myn\.cursor\projects\c-Users-r1myn-IdeaProjects-YBLK-Fashion\assets\c__Users_r1myn_AppData_Roaming_Cursor_User_workspaceStorage_7246fbf9664707dbb2d60d1c2d8d13b8_images_sigarette-53af98df-86bc-4391-b015-1c41787514e9.png"
$outDir = "C:\Users\r1myn\IdeaProjects\YBLK Fashion\src\main\resources\assets\fashion\textures\models"

$baseImg = [System.Drawing.Image]::FromFile($src)
$bmp = New-Object System.Drawing.Bitmap $baseImg
$baseImg.Dispose()

function Clone-Bmp([System.Drawing.Bitmap]$s) {
    $n = New-Object System.Drawing.Bitmap $s.Width, $s.Height, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($n)
    $g.CompositingMode = [System.Drawing.Drawing2D.CompositingMode]::SourceCopy
    $g.DrawImage($s, 0, 0, $s.Width, $s.Height)
    $g.Dispose()
    return $n
}

function Test-Orange([System.Drawing.Color]$c) {
    return ($c.A -gt 180 -and $c.R -gt 180 -and $c.G -gt 40 -and $c.G -lt 220 -and $c.B -lt 100)
}

function Test-Smoke([System.Drawing.Color]$c) {
    return ($c.A -gt 10 -and $c.R -gt 70 -and $c.R -lt 200 `
        -and [Math]::Abs([int]$c.R - [int]$c.G) -lt 30 `
        -and [Math]::Abs([int]$c.G - [int]$c.B) -lt 30 `
        -and -not (Test-Orange $c))
}

function Clamp([int]$v, [int]$lo, [int]$hi) {
    if ($v -lt $lo) { return $lo }
    if ($v -gt $hi) { return $hi }
    return $v
}

function Get-Noise([int]$x, [int]$y, [int]$f) {
    $v = [Math]::Abs([Math]::Sin(($x + 1) * 12.9898 + ($y + 1) * 78.233 + ($f + 1) * 37.719) * 43758.5453)
    return $v - [Math]::Floor($v)
}

$smoke = New-Object System.Collections.Generic.List[object]
$tips = New-Object System.Collections.Generic.List[object]
for ($y = 0; $y -lt 64; $y++) {
    for ($x = 0; $x -lt 64; $x++) {
        $c = $bmp.GetPixel($x, $y)
        if ($c.A -lt 10) { continue }
        if (Test-Orange $c) {
            $tips.Add([pscustomobject]@{ X = $x; Y = $y; C = $c })
            continue
        }
        if ($y -ge 22 -and (Test-Smoke $c)) {
            # Shift into UV band for texOffs(0,4)+h3 → pixels 16..28 (tip at y16)
            $smoke.Add([pscustomobject]@{ X = $x; Y = $y; OrigY = $y; C = $c })
        }
    }
}
Write-Host "tips=$($tips.Count) smoke=$($smoke.Count)"

# Body without smoke
$clean = Clone-Bmp $bmp
foreach ($p in $smoke) {
    $clean.SetPixel($p.X, $p.Y, [System.Drawing.Color]::FromArgb(0, 0, 0, 0))
}
# Also clear any leftover below y16
for ($y = 16; $y -lt 64; $y++) {
    for ($x = 0; $x -lt 64; $x++) {
        $c = $clean.GetPixel($x, $y)
        if (Test-Smoke $c) {
            $clean.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(0, 0, 0, 0))
        }
    }
}

# Map original smoke y24..35 → y16..27 (same shape, tip-aligned UV)
$yShift = -8

$frames = 8
for ($f = 0; $f -lt $frames; $f++) {
    $frame = Clone-Bmp $clean
    $develop = 0.5 + 0.5 * (0.5 + 0.5 * [Math]::Sin($f * [Math]::PI / 4.0))

    foreach ($p in $smoke) {
        $hRel = ($p.OrigY - 24) / 12.0
        if ($hRel -gt $develop + 0.2) { continue }

        $wave = [Math]::Sin(($p.OrigY * 0.7) + ($f * 0.9))
        $dx = [int][Math]::Round($wave)
        $dy = [int][Math]::Round([Math]::Sin(($p.X * 0.5) + $f) * 0.5)

        $nx = $p.X + $dx
        $ny = $p.OrigY + $yShift + $dy
        if ($nx -lt 0 -or $nx -ge 16 -or $ny -lt 16 -or $ny -ge 29) { continue }

        $a = Clamp ([int]([Math]::Max(100, $p.C.A * (1.0 - $hRel * 0.3)))) 100 255
        $frame.SetPixel($nx, $ny, [System.Drawing.Color]::FromArgb($a, $p.C.R, $p.C.G, $p.C.B))
    }

    # Tip shimmer — keep art colors, light noise only
    $phase = $f * ([Math]::PI * 2.0 / $frames)
    foreach ($p in $tips) {
        $n = Get-Noise $p.X $p.Y $f
        $flick = [Math]::Sin($phase * 2.2 + $p.X * 0.8 + $p.Y) * 0.5 + 0.5
        $r = Clamp ([int]($p.C.R + ($n - 0.5) * 28 + ($flick - 0.5) * 20)) 160 255
        $g = Clamp ([int]($p.C.G + ($n - 0.5) * 22 + ($flick - 0.5) * 14)) 50 180
        $b = Clamp ([int]($p.C.B + ($n - 0.5) * 10)) 5 50
        if ($n -gt 0.85) {
            $r = Clamp ([int]($r * 0.7)) 120 220
            $g = Clamp ([int]($g * 0.65)) 30 140
        }
        $frame.SetPixel($p.X, $p.Y, [System.Drawing.Color]::FromArgb(255, $r, $g, $b))
    }

    $frame.Save((Join-Path $outDir ("cigarette_$f.png")), [System.Drawing.Imaging.ImageFormat]::Png)
    $frame.Dispose()
}

for ($f = 0; $f -lt $frames; $f++) {
    $em = New-Object System.Drawing.Bitmap 64, 64, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $gg = [System.Drawing.Graphics]::FromImage($em)
    $gg.Clear([System.Drawing.Color]::FromArgb(0, 0, 0, 0))
    $gg.Dispose()
    $phase = $f * ([Math]::PI * 2.0 / $frames)
    foreach ($p in $tips) {
        $n = Get-Noise $p.X $p.Y $f
        $flick = [Math]::Sin($phase * 2.2 + $p.X * 0.8 + $p.Y) * 0.5 + 0.5
        $r = Clamp ([int]($p.C.R + 15 + ($n - 0.5) * 30 + ($flick - 0.5) * 18)) 170 255
        $g = Clamp ([int]($p.C.G + ($n - 0.5) * 20 + ($flick - 0.5) * 12)) 55 170
        $b = Clamp ([int]($p.C.B + 5)) 8 45
        $em.SetPixel($p.X, $p.Y, [System.Drawing.Color]::FromArgb(255, $r, $g, $b))
    }
    $em.Save((Join-Path $outDir ("cigarette_e_$f.png")), [System.Drawing.Imaging.ImageFormat]::Png)
    $em.Dispose()
}

# Item/base icon: original body + smoke in UV band (static frame 0 look)
$icon = Clone-Bmp $clean
foreach ($p in $smoke) {
    $ny = $p.OrigY + $yShift
    if ($ny -ge 16 -and $ny -lt 29 -and $p.X -ge 0 -and $p.X -lt 16) {
        $icon.SetPixel($p.X, $ny, $p.C)
    }
}
$icon.Save((Join-Path $outDir "cigarette.png"), [System.Drawing.Imaging.ImageFormat]::Png)
$icon.Dispose()
$clean.Dispose()
$bmp.Dispose()
Write-Host ok
