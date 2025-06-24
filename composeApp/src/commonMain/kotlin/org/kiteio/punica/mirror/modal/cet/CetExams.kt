package org.kiteio.punica.mirror.modal.cet

data class CetExams(
    val name: String,
    // 笔试
    val written: Map<String, String>,
    // 口语
    val speaking: Map<String, String>,
    val note: String,
    val pdfUrlString: String,
)