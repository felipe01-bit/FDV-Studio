# FDV-Studio
# Sistema de Gestión - Banco Unicatolica

Proyecto de desarrollo de una aplicación de escritorio para gestionar clientes, cuentas bancarias y transacciones[cite: 3].

## Requerimientos No Funcionales (RNF)
* **RNF01:** La aplicación será de escritorio[cite: 3].
* **RNF02:** La interfaz se desarrollará con Java Swing[cite: 3].
* **RNF03:** La base de datos será MySQL[cite: 3].
* **RNF04:** La conexión se realizará mediante JDBC[cite: 3].
* **RNF05:** Los formularios deben validar campos obligatorios[cite: 3].
* **RNF06:** Los valores de las transacciones deben ser mayores que cero[cite: 3].
* **RNF07:** No se debe permitir realizar retiros superiores al saldo disponible[cite: 3].
* **RNF08:** Los mensajes al usuario deben ser claros (usar cuadros de diálogo amigables)[cite: 3].
* **RNF09:** El código debe organizarse por responsabilidades para facilitar el mantenimiento[cite: 3].

## Reglas del Negocio (RN) y Dominio
* **RN01:** Un cliente no puede tener dos registros con la misma identificación[cite: 3]. 
* **RN02:** Una cuenta debe tener un número único[cite: 3].
* **RN03:** Toda cuenta debe estar asociada obligatoriamente a un cliente[cite: 3].
* **RN04:** Toda transacción debe estar asociada obligatoriamente a una cuenta[cite: 3].
* **RN05:** El tipo de transacción debe ser Depósito o Retiro[cite: 3]. 
* **RN06:** El valor de una transacción debe ser mayor que cero[cite: 3]. 
* **RN07:** Un retiro no puede superar el saldo disponible de la cuenta[cite: 3]. 
* **RN08:** Un depósito debe aumentar el saldo de la cuenta[cite: 3].
* **RN09:** Un retiro debe disminuir el saldo de la cuenta[cite: 3].
* **RN10:** Una transacción válida debe quedar registrada en el sistema[cite: 3].
* **RN11:** Una cuenta inactiva no puede permitir nuevas transacciones[cite: 3]. 
* **RN12:** Los campos obligatorios deben validarse antes de guardar[cite: 3]. 
* **RN13:** La sesión del usuario caducará automáticamente tras 1 minuto de inactividad continua[cite: 3].
* **RN14:** Si un usuario falla el inicio de sesión 3 veces consecutivas, su cuenta será bloqueada automáticamente en el sistema[cite: 3].
