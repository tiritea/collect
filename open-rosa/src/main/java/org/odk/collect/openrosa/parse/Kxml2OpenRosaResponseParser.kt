package org.odk.collect.openrosa.parse

import org.javarosa.xform.parse.XFormParser
import org.kxml2.kdom.Document
import org.kxml2.kdom.Element
import org.odk.collect.forms.FormListItem
import org.odk.collect.forms.MediaFile
import org.odk.collect.forms.MediaFile.Type
import org.odk.collect.openrosa.forms.EntityIntegrity
import org.odk.collect.shared.strings.StringUtils.isBlank
import java.io.File

object Kxml2OpenRosaResponseParser :
    OpenRosaResponseParser {

    private const val MD5_STRING_PREFIX = "md5:"

    private const val NAMESPACE_OPENROSA_ORG_XFORMS_XFORMS_LIST =
        "http://openrosa.org/xforms/xformsList"
    private const val NAMESPACE_OPENROSA_ORG_XFORMS_XFORMS_MANIFEST =
        "http://openrosa.org/xforms/xformsManifest"

    override fun parseFormList(document: Document): List<FormListItem>? {
        // Attempt OpenRosa 1.0 parsing
        val xformsElement = try {
            document.rootElement
        } catch (e: RuntimeException) {
            return null
        }

        if (xformsElement.name != "xforms") {
            return null
        }

        if (!hasNamespace(xformsElement, NAMESPACE_OPENROSA_ORG_XFORMS_XFORMS_LIST)) {
            return null
        }

        val formList: MutableList<FormListItem> = ArrayList()
        val elements = xformsElement.childCount
        for (i in 0 until elements) {
            if (xformsElement.getType(i) != Element.ELEMENT) {
                // e.g., whitespace (text)
                continue
            }

            val xformElement = xformsElement.getElement(i)
            if (!hasNamespace(
                    xformElement,
                    NAMESPACE_OPENROSA_ORG_XFORMS_XFORMS_LIST
                )) {
                // someone else's extension?
                continue
            }

            val name = xformElement.name
            if (!name.equals("xform", ignoreCase = true)) {
                // someone else's extension?
                continue
            }

            // this is something we know how to interpret
            var formId: String? = null
            var formName: String? = null
            var version: String? = null
            var downloadUrl: String? = null
            var manifestUrl: String? = null
            var hash: String? = null
            // don't process descriptionUrl

            val fieldCount = xformElement.childCount
            for (j in 0 until fieldCount) {
                if (xformElement.getType(j) != Element.ELEMENT) {
                    // whitespace
                    continue
                }

                val child = xformElement.getElement(j)
                if (!hasNamespace(child, NAMESPACE_OPENROSA_ORG_XFORMS_XFORMS_LIST)) {
                    // someone else's extension?
                    continue
                }

                when (child.name) {
                    "formID" -> {
                        formId = XFormParser.getXMLText(child, true)
                        if (formId != null && formId.isEmpty()) {
                            formId = null
                        }
                    }
                    "name" -> {
                        formName = XFormParser.getXMLText(child, true)
                        if (formName != null && formName.isEmpty()) {
                            formName = null
                        }
                    }
                    "version" -> {
                        version = XFormParser.getXMLText(child, true)
                        if (version != null && isBlank(version)) {
                            version = null
                        }
                    }
                    "majorMinorVersion" -> {
                    }
                    "descriptionText" -> {
                    }
                    "downloadUrl" -> {
                        downloadUrl = XFormParser.getXMLText(child, true)
                        if (downloadUrl != null && downloadUrl.isEmpty()) {
                            downloadUrl = null
                        }
                    }
                    "manifestUrl" -> {
                        manifestUrl = XFormParser.getXMLText(child, true)
                        if (manifestUrl != null && manifestUrl.isEmpty()) {
                            manifestUrl = null
                        }
                    }
                    "hash" -> {
                        hash = XFormParser.getXMLText(child, true)
                        hash = if (hash != null && (hash.isEmpty() || !hash.startsWith(
                                MD5_STRING_PREFIX
                            ))) {
                            null
                        } else {
                            hash.substring(MD5_STRING_PREFIX.length)
                        }
                    }
                }
            }

            if (formId == null || downloadUrl == null || formName == null) {
                formList.clear()
                return null
            }

            formList.add(FormListItem(downloadUrl, formId, version, hash, formName, manifestUrl))
        }

        return formList
    }

    override fun parseManifest(document: Document): List<MediaFile>? {
        // Attempt OpenRosa 1.0 parsing
        val manifestElement = try {
            document.rootElement
        } catch (e: RuntimeException) {
            return null
        }

        if (manifestElement.name != "manifest") {
            return null
        }

        if (!hasNamespace(manifestElement, NAMESPACE_OPENROSA_ORG_XFORMS_XFORMS_MANIFEST)) {
            return null
        }

        val elements = manifestElement.childCount
        val files: MutableList<MediaFile> = ArrayList()
        for (i in 0 until elements) {
            if (manifestElement.getType(i) != Element.ELEMENT) {
                // e.g., whitespace (text)
                continue
            }

            val mediaFileElement = manifestElement.getElement(i)
            if (!hasNamespace(mediaFileElement, NAMESPACE_OPENROSA_ORG_XFORMS_XFORMS_MANIFEST)) {
                // someone else's extension?
                continue
            }

            val type = mediaFileElement.getAttributeValue(null, "type")

            val name = mediaFileElement.name
            if (name.equals("mediaFile", ignoreCase = true)) {
                var filename: String? = null
                var hash: String? = null
                var downloadUrl: String? = null
                var integrityUrl: String? = null
                // don't process descriptionUrl
                val childCount = mediaFileElement.childCount
                for (j in 0 until childCount) {
                    if (mediaFileElement.getType(j) != Element.ELEMENT) {
                        // e.g., whitespace (text)
                        continue
                    }

                    val child = mediaFileElement.getElement(j)
                    if (!hasNamespace(child, NAMESPACE_OPENROSA_ORG_XFORMS_XFORMS_MANIFEST)) {
                        // someone else's extension?
                        continue
                    }

                    when (child.name) {
                        "filename" -> {
                            filename = XFormParser.getXMLText(child, true)
                            if (filename != null && filename.isEmpty()) {
                                filename = null
                            }
                            if (filename != null) {
                                filename = File(filename).name
                            }
                        }
                        "hash" -> {
                            hash = XFormParser.getXMLText(child, true)
                            hash = if (hash != null && hash.isEmpty()) {
                                null
                            } else {
                                hash.substring(MD5_STRING_PREFIX.length)
                            }
                        }
                        "downloadUrl" -> {
                            downloadUrl = XFormParser.getXMLText(child, true)
                            if (downloadUrl != null && downloadUrl.isEmpty()) {
                                downloadUrl = null
                            }
                        }
                        "integrityUrl" -> {
                            integrityUrl = XFormParser.getXMLText(child, true)
                        }
                    }
                }

                if (filename == null || downloadUrl == null || hash == null) {
                    return null
                }

                val mediaFileType = when (type) {
                    "entityList" -> Type.ENTITY_LIST
                    "approvalEntityList" -> Type.APPROVAL_ENTITY_LIST
                    else -> null
                }

                files.add(MediaFile(filename, hash, downloadUrl, mediaFileType, integrityUrl))
            }
        }
        return files
    }

    override fun parseIntegrityResponse(doc: Document): List<EntityIntegrity>? {
        val data = doc.rootElement
        if (data.name != "data") {
            return null
        }

        try {
            val entities = data.getElement(null, "entities")
            val entitiesList = 0.until(entities.childCount)
                .map { entities.getElement(it) }
                .filter { it.name == "entity" }
                .map {
                    val deleted = it.getElement(null, "deleted").getChild(0) as String
                    EntityIntegrity(it.getAttributeValue(null, "id"), deleted.toBooleanStrict())
                }

            return entitiesList
        } catch (e: RuntimeException) {
            return null
        }
    }

    private fun hasNamespace(e: Element, namespace: String): Boolean {
        return e.namespace.equals(namespace, ignoreCase = true)
    }
}
