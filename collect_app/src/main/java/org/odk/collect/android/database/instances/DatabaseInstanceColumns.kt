package org.odk.collect.android.database.instances

import android.provider.BaseColumns

object DatabaseInstanceColumns : BaseColumns {

    // instance column names
    const val DISPLAY_NAME = "displayName"
    const val SUBMISSION_URI = "submissionUri"
    const val INSTANCE_FILE_PATH = "instanceFilePath"
    const val JR_FORM_ID = "jrFormId"
    const val JR_VERSION = "jrVersion"
    const val STATUS = "status"
    const val CAN_EDIT_WHEN_COMPLETE = "canEditWhenComplete" // the only reason why a finalized form should not be opened for review is that it is encrypted
    const val LAST_STATUS_CHANGE_DATE = "date"
    const val FINALIZATION_DATE = "finalizationDate"
    const val DELETED_DATE = "deletedDate"
    const val GEOMETRY = "geometry"
    const val GEOMETRY_TYPE = "geometryType"
    const val CAN_DELETE_BEFORE_SEND = "canDeleteBeforeSend"
    const val EDIT_OF = "editOf"
    const val EDIT_NUMBER = "editNumber"
}
