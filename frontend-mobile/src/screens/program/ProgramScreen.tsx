import { useState } from 'react';
import { View, Text, TextInput, FlatList, TouchableOpacity, StyleSheet, Image, ActivityIndicator, SafeAreaView } from 'react-native';
import { useQuery } from '@tanstack/react-query';
import { getMovies } from '../../api/movies';

const GENRES = ['', 'ACTION', 'DRAMA', 'COMEDY', 'HORROR', 'SCI_FI', 'ANIMATION', 'THRILLER', 'ROMANCE'];

export default function ProgramScreen({ navigation }: any) {
  const [search, setSearch] = useState('');
  const [genre, setGenre] = useState('');
  const { data: movies, isLoading } = useQuery({
    queryKey: ['movies', genre, search],
    queryFn: () => getMovies({ genre: genre || undefined, title: search || undefined }),
  });

  return (
    <SafeAreaView style={styles.container}>
      <Text style={styles.heading}>Now Showing</Text>
      <TextInput style={styles.search} placeholder="Search movies…" value={search} onChangeText={setSearch} />
      <View style={styles.genreRow}>
        {GENRES.map(g => (
          <TouchableOpacity key={g} style={[styles.genreBtn, genre === g && styles.genreBtnActive]} onPress={() => setGenre(g)}>
            <Text style={[styles.genreBtnText, genre === g && styles.genreBtnTextActive]}>{g || 'All'}</Text>
          </TouchableOpacity>
        ))}
      </View>
      {isLoading ? <ActivityIndicator color="#e94560" style={{ marginTop: 32 }} /> : (
        <FlatList
          data={movies}
          keyExtractor={m => String(m.id)}
          numColumns={2}
          columnWrapperStyle={styles.row}
          contentContainerStyle={{ paddingBottom: 20 }}
          renderItem={({ item: m }) => (
            <TouchableOpacity style={styles.card} onPress={() => navigation.navigate('MovieDetail', { movie: m })}>
              {m.posterUrl ? (
                <Image source={{ uri: m.posterUrl }} style={styles.poster} />
              ) : (
                <View style={[styles.poster, styles.posterPlaceholder]}>
                  <Text style={styles.posterText}>{m.title[0]}</Text>
                </View>
              )}
              <Text style={styles.movieTitle} numberOfLines={2}>{m.title}</Text>
              <Text style={styles.movieMeta}>{m.genre} · {m.durationMinutes}min</Text>
            </TouchableOpacity>
          )}
          ListEmptyComponent={<Text style={styles.empty}>No movies found.</Text>}
        />
      )}
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f5f5', padding: 16 },
  heading: { fontSize: 22, fontWeight: '700', color: '#1a1a2e', marginBottom: 12 },
  search: { backgroundColor: '#fff', borderRadius: 8, padding: 10, fontSize: 14, marginBottom: 10, borderWidth: 1, borderColor: '#ddd' },
  genreRow: { flexDirection: 'row', flexWrap: 'wrap', gap: 6, marginBottom: 14 },
  genreBtn: { paddingHorizontal: 10, paddingVertical: 5, borderRadius: 16, backgroundColor: '#eee' },
  genreBtnActive: { backgroundColor: '#e94560' },
  genreBtnText: { fontSize: 11, color: '#555' },
  genreBtnTextActive: { color: '#fff' },
  row: { justifyContent: 'space-between' },
  card: { width: '48%', backgroundColor: '#fff', borderRadius: 8, marginBottom: 14, overflow: 'hidden', elevation: 2 },
  poster: { width: '100%', height: 140 },
  posterPlaceholder: { backgroundColor: '#1a1a2e', alignItems: 'center', justifyContent: 'center' },
  posterText: { fontSize: 40, color: '#e94560' },
  movieTitle: { fontSize: 13, fontWeight: '600', color: '#1a1a2e', padding: 8, paddingBottom: 2 },
  movieMeta: { fontSize: 11, color: '#888', paddingHorizontal: 8, paddingBottom: 8 },
  empty: { color: '#999', textAlign: 'center', marginTop: 40 },
});
