Add-Type -AssemblyName System.Drawing
$smokeSheetPath = "C:\Users\r1myn\.cursor\projects\c-Users-r1myn-IdeaProjects-YBLK-Fashion\assets\c__Users_r1myn_AppData_Roaming_Cursor_User_workspaceStorage_7246fbf9664707dbb2d60d1c2d8d13b8_images__________-2-391d4e7d-0909-46db-a213-2af5acde5aac.png"
$models = "C:\Users\r1myn\IdeaProjects\YBLK Fashion\src\main\resources\assets\fashion\textures\models"

function Load-Unlocked([string]$path) {
    $bytes = [System.IO.File]::ReadAllBytes($path)
    $ms = New-Object System.IO.MemoryStream (, $bytes)
    $img = [System.Drawing.Image]::FromStream($ms)
    $bmp = New-Object System.Drawing.Bitmap $img
    $img.Dispose()
    $ms.Dispose()
    return $bmp
}

function Save-Unlocked([System.Drawing.Bitmap]$bmp, [string]$path) {
    $tmp = "$path.__tmp.png"
    $bmp.Save($tmp, [System.Drawing.Imaging.ImageFormat]::Png)
    Move-Item -Force $tmp $path
}

function Get-SmokePixel([System.Drawing.Bitmap]$src, [int]$sx, [double]$sy) {
    # Half-pixel down: sample between sy and sy+1 with linear blend
    if ($sx -lt 0 -or $sx -ge $src.Width) {
        return [System.Drawing.Color]::FromArgb(0, 0, 0, 0)
    }
    $y0 = [int][Math]::Floor($sy)
    $y1 = $y0 + 1
    $t = $sy - $y0
    if ($y0 -lt 0) {
        return [System.Drawing.Color]::FromArgb(0, 0, 0, 0)
    }
    if ($y0 -ge $src.Height) {
        return [System.Drawing.Color]::FromArgb(0, 0, 0, 0)
    }
    $c0 = $src.GetPixel($sx, $y0)
    if ($y1 -ge $src.Height) {
        $c1 = [System.Drawing.Color]::FromArgb(0, 0, 0, 0)
    } else {
        $c1 = $src.GetPixel($sx, $y1)
    }

    $a0 = if ($c0.R -lt 18 -and $c0.G -lt 18 -and $c0.B -lt 18) { 0 } else { [int]$c0.A }
    $a1 = if ($c1.R -lt 18 -and $c1.G -lt 18 -and $c1.B -lt 18) { 0 } else { [int]$c1.A }
    if ($a0 -eq 0 -and $a1 -eq 0) {
        return [System.Drawing.Color]::FromArgb(0, 0, 0, 0)
    }

    $outA = [int]($a0 * (1.0 - $t) + $a1 * $t)
    if ($outA -lt 8) {
        return [System.Drawing.Color]::FromArgb(0, 0, 0, 0)
    }

    $r0 = [int]$c0.R; $g0 = [int]$c0.G; $b0 = [int]$c0.B
    $r1 = [int]$c1.R; $g1 = [int]$c1.G; $b1 = [int]$c1.B
    if ($a0 -eq 0) { $r0 = $r1; $g0 = $g1; $b0 = $b1 }
    if ($a1 -eq 0) { $r1 = $r0; $g1 = $g0; $b1 = $b0 }

    $r = [int]($r0 * (1.0 - $t) + $r1 * $t)
    $g = [int]($g0 * (1.0 - $t) + $g1 * $t)
    $b = [int]($b0 * (1.0 - $t) + $b1 * $t)
    return [System.Drawing.Color]::FromArgb([Math]::Min(255, $outA), $r, $g, $b)
}

$sheet = Load-Unlocked $smokeSheetPath
Write-Host "sheet $($sheet.Width)x$($sheet.Height)"

for ($f = 0; $f -lt 8; $f++) {
    $path = Join-Path $models ("cigarette_$f.png")
    $frame = Load-Unlocked $path
    for ($y = 16; $y -lt 32; $y++) {
        for ($x = 0; $x -lt 16; $x++) {
            $frame.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(0, 0, 0, 0))
        }
    }
    for ($y = 0; $y -lt 16; $y++) {
        for ($x = 0; $x -lt 16; $x++) {
            # Shift content down 0.5px: dest row y comes from source y - 0.5
            $c = Get-SmokePixel $sheet ($f * 16 + $x) ($y - 0.5)
            if ($c.A -ge 8) {
                $frame.SetPixel($x, $y + 16, $c)
            }
        }
    }
    Save-Unlocked $frame $path
    $frame.Dispose()
    Write-Host "frame $f"
}

$c0 = Load-Unlocked (Join-Path $models "cigarette_0.png")
$base = Load-Unlocked (Join-Path $models "cigarette.png")
for ($y = 16; $y -lt 32; $y++) {
    for ($x = 0; $x -lt 16; $x++) {
        $base.SetPixel($x, $y, $c0.GetPixel($x, $y))
    }
}
Save-Unlocked $base (Join-Path $models "cigarette.png")
$base.Dispose()
$c0.Dispose()
$sheet.Dispose()
Write-Host ok
