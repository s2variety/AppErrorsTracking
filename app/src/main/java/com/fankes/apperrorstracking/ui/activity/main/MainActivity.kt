/*
 * AppErrorsTracking - Added more features to app's crash dialog, fixed custom rom deleted dialog, the best experience to Android developer.
 * Copyright (C) 2019-2022 Fankes Studio(qzmmcn@163.com)
 * https://github.com/KitsunePie/AppErrorsTracking
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 *
 * This file is Created by fankes on 2022/5/14.
 */
@file:Suppress("SetTextI18n")

package com.fankes.apperrorstracking.ui.activity.main

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.view.isVisible
import com.fankes.apperrorstracking.BuildConfig
import com.fankes.apperrorstracking.R
import com.fankes.apperrorstracking.data.DataConst
import com.fankes.apperrorstracking.databinding.ActivityMainBinding
import com.fankes.apperrorstracking.locale.LocaleString
import com.fankes.apperrorstracking.ui.activity.base.BaseActivity
import com.fankes.apperrorstracking.ui.activity.errors.AppErrorsMutedActivity
import com.fankes.apperrorstracking.ui.activity.errors.AppErrorsRecordActivity
import com.fankes.apperrorstracking.utils.factory.navigate
import com.fankes.apperrorstracking.utils.factory.openBrowser
import com.fankes.apperrorstracking.utils.factory.showDialog
import com.fankes.apperrorstracking.utils.factory.toast
import com.fankes.apperrorstracking.utils.tool.FrameworkTool
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.factory.modulePrefs

class MainActivity : BaseActivity<ActivityMainBinding>() {

    companion object {

        /** 模块是否有效 */
        var isModuleValied = false
    }

    override fun onCreate() {
        binding.mainTextVersion.text = LocaleString.moduleVersion(BuildConfig.VERSION_NAME)
        binding.mainTextSystemVersion.text =
            LocaleString.systemVersion("${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT}) ${Build.DISPLAY}")
        binding.onlyShowErrorsInFrontSwitch.isChecked = modulePrefs.get(DataConst.ENABLE_ONLY_SHOW_ERRORS_IN_FRONT)
        binding.onlyShowErrorsInMainProcessSwitch.isChecked = modulePrefs.get(DataConst.ENABLE_ONLY_SHOW_ERRORS_IN_MAIN)
        binding.alwaysShowsReopenAppOptionsSwitch.isChecked = modulePrefs.get(DataConst.ENABLE_ALWAYS_SHOWS_REOPEN_APP_OPTIONS)
        binding.enableAppsConfigsTemplateSwitch.isChecked = modulePrefs.get(DataConst.ENABLE_APP_CONFIG_TEMPLATE)
        binding.hideIconInLauncherSwitch.isChecked = modulePrefs.get(DataConst.ENABLE_HIDE_ICON)
        binding.mgrAppsConfigsTemplateButton.isVisible = modulePrefs.get(DataConst.ENABLE_APP_CONFIG_TEMPLATE)
        binding.hideIconInLauncherSwitch.setOnCheckedChangeListener { btn, b ->
            if (btn.isPressed.not()) return@setOnCheckedChangeListener
            modulePrefs.put(DataConst.ENABLE_HIDE_ICON, b)
            packageManager.setComponentEnabledSetting(
                ComponentName(packageName, "${BuildConfig.APPLICATION_ID}.Home"),
                if (b) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
        }
        binding.onlyShowErrorsInFrontSwitch.setOnCheckedChangeListener { btn, b ->
            if (btn.isPressed.not()) return@setOnCheckedChangeListener
            modulePrefs.put(DataConst.ENABLE_ONLY_SHOW_ERRORS_IN_FRONT, b)
        }
        binding.onlyShowErrorsInMainProcessSwitch.setOnCheckedChangeListener { btn, b ->
            if (btn.isPressed.not()) return@setOnCheckedChangeListener
            modulePrefs.put(DataConst.ENABLE_ONLY_SHOW_ERRORS_IN_MAIN, b)
        }
        binding.alwaysShowsReopenAppOptionsSwitch.setOnCheckedChangeListener { btn, b ->
            if (btn.isPressed.not()) return@setOnCheckedChangeListener
            modulePrefs.put(DataConst.ENABLE_ALWAYS_SHOWS_REOPEN_APP_OPTIONS, b)
        }
        binding.enableAppsConfigsTemplateSwitch.setOnCheckedChangeListener { btn, b ->
            if (btn.isPressed.not()) return@setOnCheckedChangeListener
            binding.mgrAppsConfigsTemplateButton.isVisible = b
            modulePrefs.put(DataConst.ENABLE_APP_CONFIG_TEMPLATE, b)
        }
        /** 管理应用配置模板按钮点击事件 */
        binding.mgrAppsConfigsTemplateButton.setOnClickListener { whenActivated { navigate<ConfigureActivity>() } }
        /** 功能管理按钮点击事件 */
        binding.viewErrorsRecordButton.setOnClickListener { whenActivated { navigate<AppErrorsRecordActivity>() } }
        binding.viewMutedErrorsAppsButton.setOnClickListener { whenActivated { navigate<AppErrorsMutedActivity>() } }
        /** 重启按钮点击事件 */
        binding.titleRestartIcon.setOnClickListener { FrameworkTool.restartSystem(context = this) }
        /** 项目地址按钮点击事件 */
        binding.titleGithubIcon.setOnClickListener { openBrowser(url = "https://github.com/KitsunePie/AppErrorsTracking") }
        /** 显示开发者提示 */
        if (modulePrefs.get(DataConst.SHOW_DEVELOPER_NOTICE))
            showDialog {
                title = LocaleString.developerNotice
                msg = LocaleString.developerNoticeTip
                confirmButton(LocaleString.gotIt) { modulePrefs.put(DataConst.SHOW_DEVELOPER_NOTICE, value = false) }
                noCancelable()
            }
    }

    /** 刷新模块状态 */
    private fun refreshModuleStatus() {
        binding.mainLinStatus.setBackgroundResource(
            when {
                YukiHookAPI.Status.isXposedModuleActive && isModuleValied.not() -> R.drawable.bg_yellow_round
                YukiHookAPI.Status.isXposedModuleActive -> R.drawable.bg_green_round
                else -> R.drawable.bg_dark_round
            }
        )
        binding.mainImgStatus.setImageResource(
            when {
                YukiHookAPI.Status.isXposedModuleActive -> R.mipmap.ic_success
                else -> R.mipmap.ic_warn
            }
        )
        binding.mainTextStatus.text =
            when {
                YukiHookAPI.Status.isXposedModuleActive && isModuleValied.not() -> LocaleString.moduleNotFullyActivated
                YukiHookAPI.Status.isXposedModuleActive -> LocaleString.moduleIsActivated
                else -> LocaleString.moduleNotActivated
            }
        binding.mainTextApiWay.isVisible = YukiHookAPI.Status.isXposedModuleActive
        binding.mainTextApiWay.text = "Activated by ${YukiHookAPI.Status.executorName} API ${YukiHookAPI.Status.executorVersion}"
    }

    /**
     * 当模块激活后才能执行相应功能
     * @param callback 激活后回调
     */
    private inline fun whenActivated(callback: () -> Unit) {
        if (YukiHookAPI.Status.isXposedModuleActive) callback() else toast(LocaleString.moduleNotActivated)
    }

    override fun onResume() {
        super.onResume()
        /** 刷新模块状态 */
        refreshModuleStatus()
        /** 检查模块激活状态 */
        FrameworkTool.checkingActivated(context = this) { isValied ->
            isModuleValied = isValied
            refreshModuleStatus()
        }
    }
}