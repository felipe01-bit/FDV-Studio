# FDV-Studio
# Sistema de Gestión - Banco Unicatolica

Proyecto de desarrollo de una aplicación de escritorio para gestionar clientes, cuentas bancarias y transacciones[cite: 3].

## Requerimientos No Funcionales (RNF)
RNF01: La aplicación será de escritorio.
RNF02: La interfaz se desarrollará con Java Swing.
RNF03: La base de datos será MySQL.
RNF04: La conexión se realizará mediante JDBC.
RNF05: Los formularios deben validar campos obligatorios.
RNF06: Los valores de las transacciones deben ser mayores que cero.
RNF07: No se debe permitir realizar retiros superiores al saldo disponible.
RNF08: Los mensajes al usuario deben ser claros (usar cuadros de diálogo amigables).
RNF09: El código debe organizarse por responsabilidades para facilitar el mantenimiento.

## Reglas del Negocio (RN) y Dominio
RN01: Un cliente no puede tener dos registros con la misma identificación. (UI: Mensaje de error claro si el ID ya existe).
RN02: Una cuenta debe tener un número único.
RN03: Toda cuenta debe estar asociada obligatoriamente a un cliente.
RN04: Toda transacción debe estar asociada obligatoriamente a una cuenta.
RN05: El tipo de transacción debe ser Depósito o Retiro. (UI: Solo permitir estas dos opciones).
RN06: El valor de una transacción debe ser mayor que cero. (UI: Bloquear caracteres negativos).
RN07: Un retiro no puede superar el saldo disponible de la cuenta. (UI: Alerta de "Fondos Insuficientes").
RN08: Un depósito debe aumentar el saldo de la cuenta.
RN09: Un retiro debe disminuir el saldo de la cuenta.
RN10: Una transacción válida debe quedar registrada en el sistema.
RN11: Una cuenta inactiva no puede permitir nuevas transacciones. (UI: Deshabilitar el botón de procesar si la cuenta está inactiva).
RN12: Los campos obligatorios deben validarse antes de guardar. (UI: Asteriscos rojos o bordes resaltados en campos obligatorios).
RN13: La sesión del usuario caducará automáticamente tras 1 minuto de inactividad continua.
RN14: Si un usuario falla el inicio de sesión 3 veces consecutivas, su cuenta será bloqueada automáticamente en el sistema.
RN15: Si una cuenta entra en estado de "Bloqueo Preventivo" por emergencia, el sistema rechazará automáticamente cualquier retiro (incluso si hay saldo) y solo un Supervisor podrá desbloquearla.
