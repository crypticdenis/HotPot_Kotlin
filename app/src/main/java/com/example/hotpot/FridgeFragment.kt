import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.hotpot.R

class FridgeFragment : Fragment() {
    private val meatList = listOf("Meat1", "Meat2", "Meat3")
    private val vegeList = listOf("Vegetable1", "Vegetable2", "Vegetable3")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fridge, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hier kannst du auf die Ansichten in deinem Fragment zugreifen
        val linearLayout: LinearLayout = view.findViewById(R.id.fridgeLinearLayoutList)

        // Hier wird das Argument für die ausgewählte Kategorie abgerufen
        val selectedCategory = arguments?.getString("selectedCategory")

        // Hier wird die entsprechende Liste basierend auf der ausgewählten Kategorie (z.B., "Meat") gewählt
        val categoryList = when (selectedCategory) {
            "Meat" -> meatList
            "Vege" -> vegeList
            // Hier fügst du weitere Kategorien hinzu
            else -> emptyList()
        }

        // Füge dynamisch TextViews für jedes Element in der Liste hinzu
        for (item in categoryList) {
            val textView = TextView(requireContext())
            textView.text = item
            linearLayout.addView(textView)
        }
    }
}
