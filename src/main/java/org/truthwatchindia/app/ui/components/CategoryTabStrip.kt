package org.truthwatchindia.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.truthwatchindia.app.data.model.WpCategory
import org.truthwatchindia.app.ui.theme.CrimsonMaroon
import org.truthwatchindia.app.ui.theme.DarkCharcoal
import org.truthwatchindia.app.ui.theme.DividerStroke
import org.truthwatchindia.app.ui.theme.MetadataGray

data class NavigationTabItem(
    val id: Long?,
    val name: String
)

@Composable
fun CategoryTabStrip(
    categories: List<WpCategory>,
    selectedCategoryId: Long?,
    onCategorySelected: (Long?, String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Exact menu order matching web menu
    val preferredOrder = listOf(
        "देश", "विदेश", "उत्तर प्रदेश", "लखनऊ", "उत्तराखंड", "राज्य",
        "विशेष", "बिजनेस", "मनोरंजन", "खेल", "धर्म", "टेक्नोलॉजी", "सेहत", "करियर"
    )

    val orderedCategories = categories
        .filter { it.name in preferredOrder }
        .sortedBy { preferredOrder.indexOf(it.name) }

    val allTabs = listOf(NavigationTabItem(null, "होम")) + orderedCategories.map {
        NavigationTabItem(it.id, it.name)
    }

    val selectedIndex = allTabs.indexOfFirst { it.id == selectedCategoryId }.let {
        if (it >= 0) it else 0
    }

    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        containerColor = Color.White,
        contentColor = CrimsonMaroon,
        edgePadding = 12.dp,
        divider = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(DividerStroke)
            )
        },
        indicator = { tabPositions ->
            if (selectedIndex < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                    height = 2.dp,
                    color = CrimsonMaroon
                )
            }
        },
        modifier = modifier.fillMaxWidth()
    ) {
        allTabs.forEachIndexed { index, tabItem ->
            val isSelected = selectedIndex == index
            Tab(
                selected = isSelected,
                onClick = { onCategorySelected(tabItem.id, tabItem.name) },
                text = {
                    Text(
                        text = tabItem.name,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) CrimsonMaroon else DarkCharcoal
                    )
                },
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}
