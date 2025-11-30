package com.example.calculadora_kotlin

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calculadora_kotlin.databinding.ActivityMainBinding
import kotlin.math.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var numero1 = 0.0
    private var oper = 0 // 0->nada, 1->+, 2->-, 3->*, 4->/, 5->^ (potencia)

    // Bandera para saber si el número en pantalla es un resultado
    private var esResultado = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.tvNum2.text = "0"
    }

    /**
     * Gestiona la entrada de dígitos y constantes.
     */
    fun pressdigit(view: View) {
        var numActual = binding.tvNum2.text.toString()

        // Si la pantalla muestra un resultado, la próxima entrada de dígito la limpiará.
        if (esResultado && view.id != R.id.btnPunto) {
            numActual = ""
        }
        esResultado = false // Cualquier dígito presionado anula el estado de "resultado".

        // Casos especiales para constantes que reemplazan el número actual.
        when (view.id) {
            R.id.btnPi -> {
                binding.tvNum2.text = Math.PI.toString()
                return
            }
            R.id.btnE -> {
                binding.tvNum2.text = Math.E.toString()
                return
            }
        }

        val textoAAgregar = when (view.id) {
            R.id.btn0 -> "0"
            R.id.btn1 -> "1"
            R.id.btn2 -> "2"
            R.id.btn3 -> "3"
            R.id.btn4 -> "4"
            R.id.btn5 -> "5"
            R.id.btn6 -> "6"
            R.id.btn7 -> "7"
            R.id.btn8 -> "8"
            R.id.btn9 -> "9"
            R.id.btnPunto -> {
                // Lógica mejorada para el botón de punto.
                if (numActual.contains(".")) {
                    return // Si ya hay un punto, no hacer nada.
                }
                if (numActual.isEmpty()) {
                    "0." // Si la pantalla está vacía, empezar con "0.".
                } else {
                    "." // Si no, simplemente añadir el punto.
                }
            }
            else -> ""
        }

        if (numActual == "0" && textoAAgregar != ".") {
            // Reemplaza el "0" inicial si se presiona un dígito.
            binding.tvNum2.text = textoAAgregar
        } else {
            // Concatena en los demás casos.
            binding.tvNum2.text = numActual + textoAAgregar
        }
    }

    /**
     * Gestiona las operaciones binarias (+, -, *, /, ^).
     */
    fun clickop(view: View) {
        if (binding.tvNum2.text.isNotEmpty()) {
            numero1 = binding.tvNum2.text.toString().toDoubleOrNull() ?: 0.0
            val num2_text = binding.tvNum2.text.toString()

            val simboloOperador = when (view.id) {
                R.id.btnSuma -> { oper = 1; "+" }
                R.id.btnResta -> { oper = 2; "-" }
                R.id.btnMult -> { oper = 3; "*" }
                R.id.btnDiv -> { oper = 4; "/" }
                R.id.btnPow -> { oper = 5; "^" }
                else -> ""
            }

            binding.tvNum1.text = "$num2_text$simboloOperador"
            binding.tvNum2.text = "" // Limpiar para el segundo número
            esResultado = false // Al iniciar una operación, se resetea la bandera.
        }
    }

    /**
     * Gestiona las operaciones científicas unarias (sin, cos, etc.).
     */
    fun clickOpCientifica(view: View) {
        if (binding.tvNum2.text.isEmpty()) return

        val numeroActual = binding.tvNum2.text.toString().toDoubleOrNull() ?: 0.0
        var resultado = 0.0
        var operacionTexto = ""

        when(view.id) {
            R.id.btnSin -> {
                resultado = sin(Math.toRadians(numeroActual))
                operacionTexto = "sin($numeroActual)"
            }
            R.id.btnCos -> {
                resultado = cos(Math.toRadians(numeroActual))
                operacionTexto = "cos($numeroActual)"
            }
            R.id.btnTan -> {
                resultado = tan(Math.toRadians(numeroActual))
                operacionTexto = "tan($numeroActual)"
            }
            R.id.btnLog -> {
                if (numeroActual > 0) {
                    resultado = log10(numeroActual)
                    operacionTexto = "log($numeroActual)"
                } else {
                    binding.tvNum2.text = "Error"
                    esResultado = true
                    return
                }
            }
            R.id.btnLn -> {
                if (numeroActual > 0) {
                    resultado = ln(numeroActual)
                    operacionTexto = "ln($numeroActual)"
                } else {
                    binding.tvNum2.text = "Error"
                    esResultado = true
                    return
                }
            }
            R.id.btnSqrt -> {
                if (numeroActual >= 0) {
                    resultado = sqrt(numeroActual)
                    operacionTexto = "√($numeroActual)"
                } else {
                    binding.tvNum2.text = "Error"
                    esResultado = true
                    return
                }
            }
        }

        binding.tvNum1.text = operacionTexto
        formatearYMostrarResultado(resultado)
        esResultado = true // Marcar que es un resultado.
    }

    /**
     * Limpia la pantalla y reinicia las variables de estado.
     */
    fun clicBorrar(view: View) {
        binding.tvNum1.text = ""
        binding.tvNum2.text = "0"
        numero1 = 0.0
        oper = 0
        esResultado = false // Resetear la bandera al borrar.
    }

    /**
     * Calcula y muestra el resultado de la operación binaria.
     */
    fun clicIgual(view: View) {
        if (binding.tvNum2.text.isNotEmpty() && oper != 0) {
            val numero2 = binding.tvNum2.text.toString().toDoubleOrNull() ?: 0.0
            var resultado = 0.0

            when (oper) {
                1 -> resultado = numero1 + numero2
                2 -> resultado = numero1 - numero2
                3 -> resultado = numero1 * numero2
                4 -> {
                    if (numero2 != 0.0) {
                        resultado = numero1 / numero2
                    } else {
                        binding.tvNum2.text = "Error Div 0"
                        esResultado = true
                        return
                    }
                }
                5 -> resultado = numero1.pow(numero2)
            }

            formatearYMostrarResultado(resultado)
            binding.tvNum1.text = ""
            oper = 0
            esResultado = true // Marcar que el número en pantalla es un resultado.
        }
    }

    /**
     * Muestra los resultados sin el ".0" si son números enteros.
     */
    private fun formatearYMostrarResultado(resultado: Double) {
        if (resultado % 1.0 == 0.0 && !resultado.isInfinite() && !resultado.isNaN()) {
            // Es un número entero, lo mostramos sin decimales.
            binding.tvNum2.text = resultado.toLong().toString()
        } else {
            // Es un número decimal, Infinito o NaN, lo mostramos como está.
            binding.tvNum2.text = resultado.toString()
        }
    }
}
