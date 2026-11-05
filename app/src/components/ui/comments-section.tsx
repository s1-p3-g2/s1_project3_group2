import { StyleSheet, Text, View } from 'react-native';

type CommentSectionProps = {
    name: string;
    comment: string;
    };

export function CommentPage({

}: CommentSectionProps) {
        return (
            <View style={styles.card}>
        <Text style={styles.name}>{name}</Text>
        <Text>{comment}</Text>
       </View>
        );
    }