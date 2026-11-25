$files = @(
    "pl\edu\ug\cmms\report\Report.java",
    "pl\edu\ug\cmms\report\ReportData.java"
)

$base = ".\src\main\java"
$backup = "$env:TEMP\Lab2-cloud-backups"

New-Item -ItemType Directory -Force -Path $backup | Out-Null

foreach ($file in $files) {
    $source = Join-Path $base $file

    if (Test-Path -LiteralPath $source -PathType Leaf) {
        $name = Split-Path $file -Leaf

        Copy-Item -LiteralPath $source -Destination "$env:TEMP\$name"
        Move-Item -LiteralPath $source -Destination "$backup\$name"
        Move-Item -LiteralPath "$env:TEMP\$name" -Destination $source

        Write-Host "Naprawiono: $name"
    }
}