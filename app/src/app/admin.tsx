import { View } from 'react-native';
import { Colors } from '@/constants/theme';

/**
 * @author Victoria Ha
 * @version 0.1.0
 * Description: Placeholder admin console for Rate My Duck.
 *              Admin functionality will be added in a separate PR.
 * @since 11/17/2026
 */

export default function AdminScreen() {
    //TODO: make the admin screen(the duck emoji in the header at the top fo every screen is a secret button that redirects admins and does nothing for everyone else.
    return (
        <View
            style={{
                flex: 1,
                backgroundColor: Colors.light.background,
            }}
        />
    );
}