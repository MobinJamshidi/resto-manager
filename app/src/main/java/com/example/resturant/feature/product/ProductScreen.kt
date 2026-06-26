package com.example.resturant.feature.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.resturant.feature.product.data.Ingredient
import com.example.resturant.feature.product.data.Product
import com.example.resturant.feature.product.data.ProductCategory

private val Bg = Color(0xFF000000)
private val CardBg = Color(0xFF1C1C1E)
private val ChipBg = Color(0xFF2C2C2E)
private val Accent = Color(0xFF2E9E4F)
private val TextPrimary = Color.White
private val TextSecondary = Color(0xFF9A9A9E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    onBack: () -> Unit = {},
    viewModel: ProductViewModel = viewModel()
) {
    val products by viewModel.products.collectAsState(initial = emptyList())
    var dialogOpen by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Product?>(null) }
    var filter by remember { mutableStateOf<ProductCategory?>(null) }

    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                title = { Text("Products") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Bg,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = TextPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SelectChip("All", selected = filter == null) { filter = null }
                ProductCategory.entries.forEach { cat ->
                    SelectChip(cat.label, selected = filter == cat) { filter = cat }
                }
            }

            val filtered = if (filter == null) products else products.filter { it.category == filter }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (filtered.isEmpty()) {
                    item {
                        Text("هنوز محصولی نیست", color = TextSecondary, modifier = Modifier.padding(top = 24.dp))
                    }
                } else {
                    items(filtered, key = { it.id }) { product ->
                        ProductCard(product) {
                            editing = product
                            dialogOpen = true
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(Color.White)
                    .clickable {
                        editing = null
                        dialogOpen = true
                    }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Add Product", color = Color(0xFF1B1B1B), fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
        }
    }

    if (dialogOpen) {
        ProductDialog(
            existing = editing,
            onDismiss = { dialogOpen = false },
            onSave = { product ->
                viewModel.save(product)
                dialogOpen = false
            },
            onDelete = { product ->
                viewModel.delete(product)
                dialogOpen = false
            }
        )
    }
}

@Composable
private fun ProductCard(product: Product, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(product.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(product.category.label, color = TextSecondary, fontSize = 12.sp)
        }
        Spacer(Modifier.height(8.dp))
        Text("Cost: ${formatAmount(product.totalCost)} تومان", color = Accent, fontWeight = FontWeight.SemiBold)
        if (product.ingredients.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            Text(
                product.ingredients.joinToString("  •  ") { it.name },
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private class IngredientInput(
    name: String = "",
    unit: String = "",
    baseQuantity: String = "",
    consumed: String = "",
    basePrice: String = ""
) {
    var name by mutableStateOf(name)
    var unit by mutableStateOf(unit)
    var baseQuantity by mutableStateOf(baseQuantity)
    var consumed by mutableStateOf(consumed)
    var basePrice by mutableStateOf(basePrice)

    val cost: Double
        get() {
            val bq = baseQuantity.toDoubleOrNull() ?: 0.0
            val cons = consumed.toDoubleOrNull() ?: 0.0
            val price = basePrice.toDoubleOrNull() ?: 0.0
            return if (bq > 0) cons / bq * price else 0.0
        }
}

@Composable
private fun ProductDialog(
    existing: Product?,
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit,
    onDelete: (Product) -> Unit
) {
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var category by remember { mutableStateOf(existing?.category ?: ProductCategory.BAR) }
    val ingredients = remember {
        mutableStateListOf<IngredientInput>().apply {
            existing?.ingredients?.forEach {
                add(IngredientInput(it.name, it.unit, plain(it.baseQuantity), plain(it.consumed), plain(it.basePrice)))
            }
            if (isEmpty()) add(IngredientInput())
        }
    }

    val totalCost = ingredients.sumOf { it.cost }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Bg)
                .safeDrawingPadding()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .background(Color.White)
                        .clickable {
                            val built = ingredients
                                .filter { it.name.isNotBlank() }
                                .map {
                                    Ingredient(
                                        name = it.name.trim(),
                                        unit = it.unit.trim(),
                                        baseQuantity = it.baseQuantity.toDoubleOrNull() ?: 0.0,
                                        consumed = it.consumed.toDoubleOrNull() ?: 0.0,
                                        basePrice = it.basePrice.toDoubleOrNull() ?: 0.0
                                    )
                                }
                            val total = built.sumOf { it.cost }
                            val product = (existing ?: Product(
                                name = "", category = category, ingredients = emptyList(), totalCost = 0.0
                            )).copy(
                                name = name.trim(),
                                category = category,
                                ingredients = built,
                                totalCost = total
                            )
                            onSave(product)
                        }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text("Save", color = Color(0xFF1B1B1B), fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                if (existing == null) "Add Product" else "Edit Product",
                color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            DarkField(name, { name = it }, "Product Name")

            Spacer(Modifier.height(16.dp))
            SectionLabel("CATEGORY")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProductCategory.entries.forEach { cat ->
                    SelectChip(cat.label, selected = category == cat) { category = cat }
                }
            }

            Spacer(Modifier.height(20.dp))
            SectionLabel("INGREDIENTS")

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ingredients.forEach { ing ->
                    IngredientEditor(ing) { ingredients.remove(ing) }
                }
            }

            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(ChipBg)
                    .clickable { ingredients.add(IngredientInput()) }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Add, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(6.dp))
                    Text("Add Ingredient", color = TextPrimary, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardBg)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total Cost", color = TextPrimary, fontWeight = FontWeight.Bold)
                Text("${formatAmount(totalCost)} تومان", color = Accent, fontWeight = FontWeight.Bold)
            }

            if (existing != null) {
                Spacer(Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(percent = 50))
                        .background(CardBg)
                        .clickable { onDelete(existing) }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Delete Product", color = Color(0xFFE53935), fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun IngredientEditor(input: IngredientInput, onRemove: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardBg)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Ingredient", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Icon(
                Icons.Filled.Close,
                contentDescription = "remove",
                tint = Color(0xFFE53935),
                modifier = Modifier
                    .size(18.dp)
                    .clickable { onRemove() }
            )
        }
        Spacer(Modifier.height(8.dp))
        DarkField(input.name, { input.name = it }, "Name (e.g. شیر)")
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DarkField(input.unit, { input.unit = it }, "Unit (liter/kilo)", modifier = Modifier.weight(1f))
            DarkField(input.baseQuantity, { input.baseQuantity = decimalFilter(it) }, "Base (e.g. 1)", KeyboardType.Decimal, Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DarkField(input.consumed, { input.consumed = decimalFilter(it) }, "Used (e.g. 0.200)", KeyboardType.Decimal, Modifier.weight(1f))
            DarkField(input.basePrice, { input.basePrice = decimalFilter(it) }, "Price of base", KeyboardType.Decimal, Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        Text("Cost: ${formatAmount(input.cost)} تومان", color = Accent, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DarkField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = TextSecondary,
            focusedLabelColor = TextSecondary,
            unfocusedLabelColor = TextSecondary,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        ),
        modifier = modifier
    )
}

@Composable
private fun SelectChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(if (selected) Accent else ChipBg)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text,
            color = if (selected) Color.White else TextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(10.dp))
}

private fun decimalFilter(s: String): String {
    val filtered = s.filter { it.isDigit() || it == '.' }
    val dot = filtered.indexOf('.')
    return if (dot == -1) filtered
    else filtered.substring(0, dot + 1) + filtered.substring(dot + 1).replace(".", "")
}

private fun plain(value: Double): String =
    if (value == value.toLong().toDouble()) value.toLong().toString() else value.toString()

private fun formatAmount(value: Double): String = "%,.0f".format(value)