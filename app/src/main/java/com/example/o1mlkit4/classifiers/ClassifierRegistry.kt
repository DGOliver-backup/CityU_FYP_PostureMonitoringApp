// File: classifiers/ClassifierRegistry.kt
package com.example.o1mlkit4.classifiers

import com.example.o1mlkit4.classifiers.ExercisePlugIn.BBRW.BarbellRowClassifier
import com.example.o1mlkit4.classifiers.ExercisePlugIn.CTPD.TricepsPulldownClassifier
import com.example.o1mlkit4.classifiers.ExercisePlugIn.DBSP.DumbbellShoulderPressClassifier
import com.example.o1mlkit4.classifiers.ExercisePlugIn.FRSQ.FrontSquadClassifier
import com.example.o1mlkit4.classifiers.ExercisePlugIn.HICC.HighCableCurlsClassifier
import com.example.o1mlkit4.classifiers.ExercisePlugIn.INCP.InclineChestPressClassaifier
import com.example.o1mlkit4.classifiers.ExercisePlugIn.PSUP.PushUpClassifier
import com.example.o1mlkit4.classifiers.ExercisePlugIn.SAPD.SitupClassifier
import com.example.o1mlkit4.classifiers.ExercisePlugIn.SAPD.StraightArmPullDownClassifier

object ClassifierRegistry {
    val classifiers: MutableList<BaseClassifier> = mutableListOf()

    private var currentClassifier: BaseClassifier? = null

    fun initializeAll() {
        classifiers.add(StraightArmPullDownClassifier())
        classifiers.add(TricepsPulldownClassifier())
        classifiers.add(BarbellRowClassifier())
        classifiers.add(DumbbellShoulderPressClassifier())
        classifiers.add(FrontSquadClassifier())
        classifiers.add(InclineChestPressClassaifier())
        classifiers.add(PushUpClassifier())
        classifiers.add(SitupClassifier())
        classifiers.add(HighCableCurlsClassifier())
        // Add  classifiers here...
    }

    fun setCurrentClassifier(classifier: BaseClassifier) {
        currentClassifier = classifier
    }

    fun getCurrentClassifier(): BaseClassifier {
        return currentClassifier ?: classifiers.first()
    }
}
