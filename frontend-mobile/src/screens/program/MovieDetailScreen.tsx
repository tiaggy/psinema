import { View, Text, ScrollView, TouchableOpacity, StyleSheet, Image, ActivityIndicator } from 'react-native';
import { useQuery } from '@tanstack/react-query';
import { getScreenings } from '../../api/screenings';

export default function MovieDetailScreen({ route, navigation }: any) {
  const { movie } = route.params;
  const { data: screenings, isLoading } = useQuery({
    queryKey: ['screenings', movie.id],
    queryFn: () => getScreenings({ movieId: movie.id }),
  });

  const upcoming = screenings?.filter(s => !s.cancelled && new Date(s.startTime) > new Date()) ?? [];

  return (
    <ScrollView style={styles.container}>
      {movie.posterUrl ? (
        <Image source={{ uri: movie.posterUrl }} style={styles.banner} />
      ) : (
        <View style={styles.bannerPlaceholder}>
          <Text style={styles.bannerText}>{movie.title}</Text>
        </View>
      )}
      <View style={styles.body}>
        <Text style={styles.title}>{movie.title}</Text>
        <View style={styles.metaRow}>
          <Text style={styles.metaBadge}>{movie.genre}</Text>
          <Text style={styles.metaBadge}>{movie.durationMinutes} min</Text>
          {movie.director && <Text style={styles.metaBadge}>{movie.director}</Text>}
        </View>
        {movie.description && <Text style={styles.description}>{movie.description}</Text>}

        <Text style={styles.sectionTitle}>Upcoming Screenings</Text>
        {isLoading ? <ActivityIndicator color="#e94560" /> : upcoming.length === 0 ? (
          <Text style={styles.empty}>No upcoming screenings.</Text>
        ) : (
          upcoming.map(s => (
            <TouchableOpacity key={s.id} style={styles.screeningRow}
              onPress={() => navigation.navigate('SeatSelection', { screening: s })}>
              <View>
                <Text style={styles.screeningTime}>{new Date(s.startTime).toLocaleString([], { weekday: 'short', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })}</Text>
                <Text style={styles.screeningHall}>{s.hall.name}</Text>
              </View>
              <View style={styles.priceBox}>
                <Text style={styles.price}>€{s.basePrice}</Text>
                <Text style={styles.buyText}>Buy →</Text>
              </View>
            </TouchableOpacity>
          ))
        )}
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f5f5' },
  banner: { width: '100%', height: 220 },
  bannerPlaceholder: { height: 160, backgroundColor: '#1a1a2e', alignItems: 'center', justifyContent: 'center' },
  bannerText: { color: '#e94560', fontSize: 24, fontWeight: '700' },
  body: { padding: 16 },
  title: { fontSize: 22, fontWeight: '700', color: '#1a1a2e', marginBottom: 8 },
  metaRow: { flexDirection: 'row', gap: 8, marginBottom: 12 },
  metaBadge: { backgroundColor: '#e94560', color: '#fff', fontSize: 11, paddingHorizontal: 8, paddingVertical: 3, borderRadius: 12 },
  description: { fontSize: 13, color: '#555', lineHeight: 20, marginBottom: 20 },
  sectionTitle: { fontSize: 16, fontWeight: '600', color: '#1a1a2e', marginBottom: 10 },
  screeningRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', backgroundColor: '#fff', borderRadius: 8, padding: 14, marginBottom: 8, elevation: 1 },
  screeningTime: { fontSize: 13, fontWeight: '600', color: '#1a1a2e' },
  screeningHall: { fontSize: 11, color: '#888', marginTop: 2 },
  priceBox: { alignItems: 'flex-end' },
  price: { fontSize: 15, fontWeight: '700', color: '#e94560' },
  buyText: { fontSize: 11, color: '#888' },
  empty: { color: '#999', fontSize: 13 },
});
