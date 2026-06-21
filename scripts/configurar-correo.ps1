$ErrorActionPreference = 'Stop'

Write-Host ''
Write-Host 'Configuracion de correo - Nueva Sonrisa' -ForegroundColor Cyan
Write-Host 'La clave no se mostrara ni se guardara en el proyecto.' -ForegroundColor DarkGray
Write-Host ''

[IntPtr]$keyPointer = [IntPtr]::Zero
try {
    $secureKey = Read-Host 'Ingrese RESEND_API_KEY (no se mostrara)' -AsSecureString
    $keyPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secureKey)
    $apiKey = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($keyPointer)
    $apiKey = $apiKey.Trim().Trim('"').Trim("'")
    if ([string]::IsNullOrWhiteSpace($apiKey) -or -not $apiKey.StartsWith('re_')) {
        throw 'La clave de Resend debe comenzar con re_.'
    }

    $sender = (Read-Host 'Remitente verificado (correo@dominio.com o Nombre <correo@dominio.com>)').Trim()
    if ($sender -match '^[^<>\s]+@[^<>\s]+$') {
        $sender = "Nueva Sonrisa <$sender>"
    }
    if ([string]::IsNullOrWhiteSpace($sender) -or $sender -notmatch '<[^<>\s]+@[^<>\s]+>') {
        throw 'El remitente no tiene un formato de correo valido.'
    }

    [Environment]::SetEnvironmentVariable('RESEND_API_KEY', $apiKey.Trim(), 'User')
    [Environment]::SetEnvironmentVariable('RESEND_FROM', $sender.Trim(), 'User')
    [Environment]::SetEnvironmentVariable('FEATURE_EMAILS', 'true', 'User')

    Set-Content -LiteralPath (Join-Path $PSScriptRoot '..\.email-configured') -Value 'configured' -Encoding Ascii
    Write-Host ''
    Write-Host 'Correo configurado correctamente.' -ForegroundColor Green
    Write-Host 'Reinicie la aplicacion para que lea las nuevas variables.' -ForegroundColor Yellow
} catch {
    Write-Host ''
    Write-Host "No se guardo la configuracion: $($_.Exception.Message)" -ForegroundColor Red
} finally {
    if ($keyPointer -ne [IntPtr]::Zero) {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($keyPointer)
    }
    $apiKey = $null
}

Write-Host ''
Read-Host 'Presione Enter para cerrar'
