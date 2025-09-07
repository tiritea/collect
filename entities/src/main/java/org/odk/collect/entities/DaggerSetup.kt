package org.odk.collect.entities

import dagger.Component
import dagger.Module
import dagger.Provides
import org.odk.collect.async.Scheduler
import org.odk.collect.entities.browser.EntityBrowserActivity
import org.odk.collect.entities.storage.EntitiesRepository
import javax.inject.Singleton

interface EntitiesDependencyComponentProvider {
    val entitiesDependencyComponent: EntitiesDependencyComponent
}

@Component(modules = [EntitiesDependencyModule::class])
@Singleton
interface EntitiesDependencyComponent {

    @Component.Builder
    interface Builder {

        fun entitiesDependencyModule(entitiesDependencyModule: EntitiesDependencyModule): Builder

        fun build(): EntitiesDependencyComponent
    }

    fun inject(entityBrowserActivity: EntityBrowserActivity)
}

@Module
open class EntitiesDependencyModule {

    @Provides
    open fun providesEntitiesRepository(): EntitiesRepository {
        throw UnsupportedOperationException("This should be overridden by dependent application")
    }

    @Provides
    open fun providesScheduler(): Scheduler {
        throw UnsupportedOperationException("This should be overridden by dependent application")
    }
}
