# Lunaris Dolby

A modern Dolby audio framework with a Compose-based UI, device integration support, and customizable user experience.

## Features

- Dolby audio processing and effects
- Modern Compose-based Dolby UI
- Per-device Dolby state handling
- Dolby media codec integration
- Custom EQ and audio profiles
- Headphone AutoEQ support
- Modern settings and navigation
- Customizable Lunaris Dolby UI
- Page Style customization
- Multiple UI layout and appearance options
- Device-specific integration support

## Lunaris Dolby UI

Lunaris Dolby provides a modern Compose interface for controlling Dolby audio features.

The UI is designed to be customizable so users can choose the appearance and layout that fits their preference instead of being locked to a single presentation.

### Page Style

Lunaris Dolby includes a customizable Page Style system.

Users can configure supported UI elements such as:

- Page layout
- Header style
- Search placement
- Title visibility
- Greeting visibility
- Avatar visibility
- Summary visibility
- Icon style
- Corner style
- Divider style
- Card appearance
- Other supported visual preferences

The selected preferences are stored locally and applied by the Lunaris Dolby UI.

A reset option is also provided to restore the default appearance.

## Requirements

Lunaris Dolby requires a compatible Android build and Dolby-supported audio stack.

The implementation is primarily intended for custom Android ROM/device trees where the required Dolby components can be integrated into the vendor and system build.

## Integration

Add the Dolby product configuration to your device tree:

```makefile
$(call inherit-product, hardware/dolby/dolby.mk)
Make sure the Dolby effects are included in the device audio effects configuration.
Dolby Media Codecs
For Dolby media codecs, add the Dolby codec configuration to the appropriate vendor media codec configuration:
<Include href="media_codecs_dolby_audio.xml" />
Your device must support the required Codec2 (C2) components for the media codec integration to work correctly.
Device Manifest
HIDL definitions should be provided through the appropriate framework/device configuration rather than unnecessarily overriding the complete device manifest from the device tree.
If your device tree currently uses:
DEVICE_FRAMEWORK_COMPATIBILITY_MATRIX_FILE :=
change it to:
DEVICE_FRAMEWORK_COMPATIBILITY_MATRIX_FILE +=
Likewise, if applicable, change:
DEVICE_MANIFEST_FILE :=
to:
DEVICE_MANIFEST_FILE +=
Using += allows the existing framework/device definitions to be extended without replacing the complete configuration.
Device Integration
A proper device integration should:
Include the Dolby product configuration.
Add the required Dolby effects to the audio effects configuration.
Add the required Dolby media codec configuration.
Ensure the device supports the required Codec2 components.
Configure the required framework/vendor compatibility entries.
Include the Lunaris Dolby UI package where required by the ROM/device tree.
Verify the required vendor libraries, permissions, and SELinux rules.
Page Style Integration
The Page Style implementation is contained inside the Lunaris Dolby UI and provides persistent user customization.
Relevant components include:
MaxxDolby/
└── src/org/lunaris/dolby/
    └── ui/
        ├── components/
        │   ├── ModernComponents.kt
        │   └── PageStylePrefs.kt
        │
        └── screens/
            ├── ModernDolbySettingsScreen.kt
            ├── Navigation.kt
            └── PageStyleScreen.kt
The Page Style preferences are separated from the main settings UI so additional customization options can be added without redesigning the complete Dolby screen.
Credits
Lunaris Dolby
Ghost — Rewrite Dolby UI in Jetpack Compose and the Lunaris Dolby foundation.
https://github.com/Ghosuto⁠�
Adithya R — AOSPA Dolby Manager initial code.
https://github.com/adithya2306⁠�
Kenway — Base/Treble changes and EQ tuning.
https://github.com/kenway214⁠�
tranQuila — Per-device Dolby state memory.
https://github.com/MrTopia⁠�
Pablo Escobar — AutoEQ headphone correction profiles.
https://github.com/pabloescobar-reborn⁠�
swiitch-OFF-Lab — Base hardware Dolby tree.
https://github.com/swiitch-OFF-Lab/hardware_dolby⁠�
Porting & Development
Samakshhhh — Lunaris Dolby porting and device integration work.
https://github.com/samakshkambxj⁠�
Anshuman X (maxxcodebug) — Lunaris Dolby port/customization work for CMF Phone 1 (Tetris), UI customization, Page Style implementation, and continued development.
https://github.com/maxxcodebug⁠�
License
Please refer to the individual source files and their respective upstream projects for applicable licensing and copyright information.
Respect the licenses and attribution requirements of all upstream projects when redistributing or modifying this software.
Disclaimer
Dolby, Dolby Atmos, and related trademarks are property of their respective owners.
This project is an independent implementation/integration for custom Android ROMs and is not affiliated with or endorsed by Dolby Laboratories.
